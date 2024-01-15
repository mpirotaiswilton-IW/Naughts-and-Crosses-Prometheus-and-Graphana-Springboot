# Dockerized-SpringBoot-Applications-Assignment-MaxPW
 
This Github repository contains a Springboot Naughts and Crosses API with a Postgres Database, a pgAdmin interface, a Prometheus monitoring system and a Grafana metrics dashboard. 

## Summary

[Dockerized-SpringBoot-Applications-Assignment-MaxPW](#dockerized-springboot-applications-assignment-maxpw)
* [Summary](#summary)
* [Setup and Pre-requisites](#setup-and-pre-requisites)
* [Running Hello-World](#running-hello-world)
* [Running Naughts-and-crosses](#running-naughts-and-crosses)
    * [Playing a game using Postman](#playing-a-game-using-postman)
        * [Optional Endpoints](#optional-endpoints)
    * [Role-Based Authorization](#role-based-authentication)
        * [Admin Endpoints](#admin-endpoints)
    * [Accessing the pgAdmin Dashboard](#accessing-the-pgadmin-dashboard)


## Setup and Pre-requisites

1. If not already installed:

-  Install Docker on your device (you can use the following link for a guide: [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/))
- Install the latest version of OpenJDK 17 on your device (The following page has a complete catalogue of OpenJDK downloads: [https://www.openlogic.com/openjdk-downloads](https://www.openlogic.com/openjdk-downloads))

2. Clone this repository or download the .zip file from GitHub (extract the downloaded zip file )

## Running Hello-World

1. Using a Command Line Interface used to run Docker and docker-compose commands, change directory to the downloaded/cloned repository, then change directory to the "/hello-world" folder

2. Run the following command: 

```
docker-compose up
```

3. A Docker container should now be running with the "Hello-World" API installed
4. Using a browser, go to [http://localhost:8080](http://localhost:8080): you will be greeted with the following text:

```
Hello World! :D
```

## Running Naughts-and-crosses

1. Using a Command Line Interface used to run Docker and docker-compose commands, change directory to the downloaded/cloned repository, then change directory to the "/naughts-and-crosses" folder

2. Run the following command: 

```
docker-compose up
```

3. 5 docker containers should now be running:
    * `naughts-and-crosses-api`: where the spring-boot api image, built using a Dockerfile, is containerized
    * `db`: where a Postgres database is containerized and used by the API
    * `naughts-and-crosses-pgadmin`: where a pgAdmin interface is containerized. This is used to monitor our Postgres database container
    * `naughts-and-crosses-prometheus` : 
    * `naughts-and-crosses-grafana`
4. As the API is now running and ready to use, we can import the `Naughts and Crosses API.postman_collection.json` file into Postman: this will include a full collection of requests you can use on the Naughts-and-crosses API

### Playing a game (using Postman)

1. Register 2 new users by running the "Register User 1" and "Register User 2" requests (localhost:8080/user/register/). In the request body, enter the new user's username and password: 
``` JSON
JSON request body format:
{
    "username":"foo",
    "password":"fooword"
}
```
1. (Cont') 
By default, these requests will create 2 new users called "foo" and "bar" with their own passwords. Feel free to change these if you wish. A successful request will return a "201 Created" response.
2. Authenticate for each new user by using the "Authenticate User" request (localhost:8080/authenticate), filling in the request body with their respective credentials (by default the request body is populated with the credentials for a user called "foo"). The JSON object format is the same as for registering a new user. Sending a successful request should yield a 200 OK response and a bearer token in the response body: Copy the token value and paste it somewhere for later use. A Token for this API will last for 12 hours.
3. Create a new game using the "Create Game" request (localhost:8080/game/). Under Postman's Authorization tab for the request or in the Authorization header, put the bearer token of the user who is creating the game (eg. user foo or bar). Inside the request body, define some game settings such as whether the player creating the new game starts first or are playing as naughts or crosses:
``` JSON
JSON request body format:
{
    "doesPrimaryUserStart" : true,
    "isPrimaryUserOs" : false
}
```
 3. (Cont') 
 A successful request should return a 201 Created response along with a JSON object of the newly created game.
4. Have a second user join the newly created game using the "Join Game" (localhost:8080/game/join/{game id}). Under Postman's Authorization tab for the request or in the Authorization header, put the bearer token of the other user who is joining the game. A successful request should return a 200 OK response along with a JSON object of the newly created game.
5. Each user can now submit a move using the "Play Move" request (localhost:8080/game/{game id}/play/{number from 1 to 9}). Don't forget each user must enter their token to be authorized to submit a new move. Users can use the second path parameter to select the square they want to choose, where 1 is the top left most square and 9 is the bottom right most square:
```
ASCII visual representation:

1 | 2 | 3
--+---+--
4 | 5 | 6
--+---+--
7 | 8 | 9
```
5. (Cont') 
A successful move will return a 200 OK response and a list of all moves played in  chronological order. Players become unable to submit a move when the game has been won or been deemed a draw, where all squares are filled and no winning position has been achieved. 

#### Optional Endpoints

* Users can also forfeit a game if they so choose using the "Forfeit Game" request (localhost:8080/game/forfeit/{game id}). Under Postman's Authorization tab for the request or in the Authorization header, put the bearer token of the user who is forfeiting the game (this user must be one of the players). A successful request should return a 200 OK response along with a JSON object of the forfeited game.
* Any user who created a game can also delete it at any time using the "Delete Game" request (localhost:8080/game/delete/{game id}). Under Postman's Authorization tab for the request or in the Authorization header, put the bearer token of the user who is deleting the game they created. A successful request should return a 204 NO_CONTENT response.
* Any given user can delete themselves from the API's database with the "Delete User Self" request (localhost:8080/user/self). Under Postman's Authorization tab for the request or in the Authorization header, put the bearer token of the user who wants to delete themselves. A successful request should return a 204 NO_CONTENT response.

### Role-based Authentication

The Naughts-and-crosses API also includes role-based authentication. This enables users with different roles to access different endpoints, or have the same endpoint behave differently.

1. The API already adds a user with administrative privileges, with a username of "DEV_ADMIN" and password "admin_pass". 
2. Authenticate "DEV_ADMIN" by using the "Authenticate User" request (localhost:8080/authenticate), filling in the request body with the credentials above. Sending a successful request should yield a 200 OK response and a bearer token in the response body: Copy the token value and paste it somewhere for later use.

#### Admin endpoints

* A user with administrative privileges can delete any game from the database at any time using the "Delete Game" request (localhost:8080/game/delete/{game id}). Under Postman's Authorization tab for the request or in the Authorization header, put the bearer token of the administrative user. A successful request should return a 204 NO_CONTENT response.
* A user with administrative privileges can delete any user from the API's database with the "Delete User Any" request (localhost:8080/user/{id}). Under Postman's Authorization tab for the request or in the Authorization header, put the bearer token of the administrative user. A successful request should return a 204 NO_CONTENT response.

### Accessing the pgAdmin Dashboard

After successfully deploying the docker containers to run the Naughts-and-crosses API, you can access the pgAdmin dashboard at [http://localhost:5050/](http://localhost:5050/)
1. Login using the pgadmin service environment variables defined in the `docker-compose.yml` file:
    * The email address field is defined under the environment variable `PGADMIN_DEFAULT_EMAIL`
    * The password field is defined under the environment variable `PGADMIN_DEFAULT_PASSWORD`

2. After you're successfully logged in, click on `Add New Server` on the Dashboard Home Page:
    * In the General tab, name your server as you see fit
    * Navigate to the Connection tab
    * For the `host name/address`, use the name of the Postgres container `db`
    * Make sure the port field is `5432`
    * the `Username` field is defined by the `POSTGRES_USER` environment variable in the `docker-compose.yml` file
    * the `Password` field is defined by the `POSTGRES_PASSWORD` environment variable in the `docker-compose.yml` file
    * Click save and, in the Object explorer, under Servers you should see your newly saved server

3. If you now navigate in the Object Explorer to `Servers>{name you gave the database server}>Databases>admin`, you will find this is the Postgres database holding the `users`, `roles`, `games` and `roles_users` tables for our API. Using the Query Tool in the `Tools` tab will allow you to view the content of each table by using SQL commands. 