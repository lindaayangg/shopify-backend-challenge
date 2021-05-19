# Shopify Backend Challenge (Fall 2021)

## Overview
This Image API allows users to add, delete, sell, and buy images.

## Endpoints

**`GET` `/users/{userId}`** [Get the information of a User by ID](GET_USER.md) \
**`POST` `/users`** [Create a User](CREATE_USER.md)

**`GET` `/images/{imageId}`** [Get an Image by ID](GET_IMAGE.md)\
**`POST` `/images`** [Upload an Image](UPLOAD_IMAGE.md)\
**`PATCH` `/images/{imageId}`** [Set the information of an Image](SET_IMAGE_INFO.md)\
**`POST` `/buy/{imageId}`** [Buy an Image](BUY_IMAGE.md)\
**`DELETE` `/images`** [Delete Images by User](DELETE_USER_IMAGES.md)

Test the endpoints with Postman: https://www.getpostman.com/collections/09b3203468e6b6e5cd74 (Import the collection)

## Database Tables

### user

| Field      | Type         | Description                                      |
|------------|--------------|--------------------------------------------------|
| id         | BIGINT(20)   | User ID                                          |
| name       | VARCHAR(255) | User name                                        |
| balance    | VARCHAR(255) | Balance of the user from selling & buying images |

### image

| Field      | Type         | Description                                      |
|------------|--------------|--------------------------------------------------|
| id         | BIGINT(20)   | Image ID                                         |
| name       | VARCHAR(255) | Image name                                       |
| permission | VARCHAR(255) | Accessing permission of the image                |
| price      | DOUBLE       | Price of the image                               |
| discount   | INT          | Discount for the image                           |
| amount     | INT          | Inventory of the image                           |
| user_id    | BIGINT(20)   | ID of owner of the image                         |

## Usage
To run the API locally, perform the following steps:
1. Execute `git clone https://github.com/lindaayangg/shopify-backend-challenge.git`
2. Execute `mvn clean install`
3. Execute `mvn spring-boot:run`

To login to the `H2` database:
1. Navigate to `http://localhost:8080/h2-console/` 
2. Login to the `H2` database with credentials in `application.properties`
