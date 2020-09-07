## Get User

### Description
Get a user's information by ID.

### Endpoint
`/users/{userId}`

### Method
`GET`

### URL Parameters
`None`

### Data Parameters
`None`

### Success Response
**Code:** `200 OK` \
**Content:**
```$xslt
{
    "id": 1,
    "name": "Linda",
    "balance": 0.0,
    "imageList": []
}
```

### Error Response
**Code:** `NOT FOUND` \
**Content:** `User with ID: $userId not found.`
