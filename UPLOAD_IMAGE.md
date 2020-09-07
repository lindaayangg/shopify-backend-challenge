## Upload Image

### Description
Upload an image.

### Endpoint
`/images`

### Method
`POST`

### URL Parameters
`None`

### Data Parameters
`userId = 1`\
`images = [upload file]`\
`permission = PRIVATE` or `permission = PUBLIC`

Note: There can be more than one `images = [upload file]` parameter.

### Success Response
**Code:** `200 OK` \
**Content:** `$number image(s) uploaded successfully!`


### Error Response
**Code:** `BAD REQUEST` \
**Content:** `No image to upload found in the request.`

OR 

**Code:** `NOT FOUND` \
**Content:** `User with ID: $userId not found.`

OR

**Code:** `INTERNAL SERVER ERROR` \
**Content:** `Failed to upload image(s).`