# WebQuizEngine

This project I've developed a multi-user web service for creating and solving quizzes using REST API, embedded database, security and other technologies. Here I have concentrated on the server side ("engine") with no user interface at all. For testing you can use Postman, browser etc.    
Details of the stages of the project can be found [here](https://hyperskill.org/projects/91).    
You can find main method in `WebQuizEngine.java` and REST-controller in `QuizRController.java`. Other classes have self-explanatory names.    

## User capabilities:

### Register a new user

When sending any requests, you must use Basic authorization. Therefore, you should start by registering a user.To register a new user, the client needs to send a JSON with email and password via `POST` request to `/api/register`:
```
{
  "email": "test@gmail.com",
  "password": "secret"
}
```
The service returns `200 (OK)` status code if the registration has been completed successfully. If the email is already taken by another user, the service will return the `400 (Bad request)` status code.    
Here are some additional restrictions to the format of user credentials:
- the email must have a valid format (with @ and .);
- the password must have at least five characters.
- If any of them is not satisfied, the service will also return the `400 (Bad request)` status code.

### Create a new quiz 

To create a new quiz, the client needs to send a JSON as the request's body via `POST` to `/api/quizzes`. The JSON should contain the four fields: title (a string), text (a string), options (an array of strings) and answer (integer index of the correct option).    
Example of request body:
```
{
  "title": "The Java Logo",
  "text": "What is depicted on the Java logo?",
  "options": ["Robot","Tea leaf","Cup of coffee","Bug"],
  "answer": 2
}
```
The answer equals 2 corresponds to the third item from the options array ("Cup of coffee"). The server response is a JSON with four fields: id, title, text and options. Here is an example.
```
{
  "id": 1,
  "title": "The Java Logo",
  "text": "What is depicted on the Java logo?",
  "options": ["Robot","Tea leaf","Cup of coffee","Bug"]
}
```
Here is another  example:
```
{
  "title": "Coffee drinks",
  "text": "Select only coffee drinks.",
  "options": ["Americano","Tea","Cappuccino","Sprite"],
  "answer": [0,2]
}
```
The answer equals `[0,2]` corresponds to the first and the third item from the options array. If the request JSON does not contain title or text, or they are empty strings, then the server responds with the `400 (Bad request)` status code. If the number of options in the quiz is less than 2, the server returns the same status code. All new quizzes will be permanently stored in a database.

### Get a quiz by id

To get a quiz by id, the client sends the `GET` request to `/api/quizzes/{id}`.    
Here is a response example:
```
{
  "id": 1,
  "title": "The Java Logo",
  "text": "What is depicted on the Java logo?",
  "options": ["Robot","Tea leaf","Cup of coffee","Bug"]
}
```
If the user is not authorized, the status code is `401 (Unauthorized)`.

### Solve the quiz

To solve the quiz, the client sends a `POST` request to `/api/quizzes/{id}/solve` and passes the answer parameter in the content. This parameter is the index of a chosen option from options array. As before, it starts from zero.
The service returns a JSON with two fields: success (true or false) and feedback (just a string). There are three possible responses.
It is also possible to send an empty array `[]` since some quizzes may not have correct options.    
If the passed answer is correct (e.g., `POST` to `/api/quizzes/1/solve` with content `answer=2`):    
`{"success":true,"feedback":"Congratulations, you're right!"}`    
If the answer is incorrect (e.g., `POST` to `/api/quizzes/1/solve` with content `answer=1`):    
`{"success":false,"feedback":"Wrong answer! Please, try again."}`    
If the specified quiz does not exist, the server returns the `404 (Not found)` status code.

### Delete the quiz

A user can delete their quiz by sending the `DELETE` request to `/api/quizzes/{id}`.    
If the user is not authorized, the status code is `401 (Unauthorized)`.    
If the operation was successful, the service returns the `204 (No content)` status code without any content.    
If the specified quiz does not exist, the server returns `404 (Not found)`.    
If the specified user is not the author of this quiz, the response is the `403 (Forbidden)` status code.    

### Get all existing quizzes

To get all existing quizzes in the service, the client sends the `GET` request to `/api/quizzes`.
The response contains a JSON with quizzes (inside content) and some additional metadata:
```
{
  "totalPages":1,
  "totalElements":3,
  "last":true,
  "first":true,
  "sort":{ },
  "number":0,
  "numberOfElements":3,
  "size":10,
  "empty":false,
  "pageable": { },
  "content":[
    {"id":102,"title":"Test 1","text":"Text 1","options":["a","b","c"]},
    {"id":103,"title":"Test 2","text":"Text 2","options":["a", "b", "c", "d"]},
    {"id":202,"title":"The Java Logo","text":"What is depicted on the Java logo?",
     "options":["Robot","Tea leaf","Cup of coffee","Bug"]}
  ]
}
```
In this regard, the API returns only 10 quizzes at once and supports the ability to specify which portion of quizzes is needed. I've simplified JSON a bit here, but in the API it is in the same format it is generated by the framework. The API should supports the navigation through pages by passing the page parameter ( `/api/quizzes?page=1`). The first page is 0 since pages start from zero, just like our quizzes. If there are no quizzes, content is empty `[]`. If the user is authorized, the status code is `200 (OK)`, otherwise, it's `401 (Unauthorized)`.

### Get completions of quizzes

The service provides ability for getting all completions of quizzes for a specified user by sending the `GET` request to `/api/quizzes/completed` together with the user auth data.
All the completions should be sorted from the most recent to the oldest.
A response is separated by pages.
It contains a JSON with quizzes (inside content) and some additional metadata as in the previous operation.
Here is a response example:
```
{
  "totalPages":1,
  "totalElements":5,
  "last":true,
  "first":true,
  "empty":false,
  "content":[
    {"id":103,"completedAt":"2019-10-29T21:13:53.779542"},
    {"id":102,"completedAt":"2019-10-29T21:13:52.324993"},
    {"id":101,"completedAt":"2019-10-29T18:59:58.387267"},
    {"id":101,"completedAt":"2019-10-29T18:59:55.303268"},
    {"id":202,"completedAt":"2019-10-29T18:59:54.033801"}
  ]
}
```
Since it is allowed to solve a quiz multiple times, the response may contain duplicate quizzes, but with the different completion date.
I removed some metadata keys from the response to keep it comprehensible.
If there are no quizzes, content is empty `[]`. If the user is authorized, the status code is `200 (OK)`; otherwise, it's `401 (Unauthorized)`.
