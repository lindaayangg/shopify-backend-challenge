## Get Image

### Description
Get an image by ID.

### Endpoint
`/images/{imageId}`

### Method
`GET`

### URL Parameters
`userId = 1`

### Data Parameters
`None`

### Success Response
**Code:** `200 OK` \
**Content:** `A resource of MediaType image/jpeg`


### Error Response
**Code:** `NOT FOUND` \
**Content:** `Image with user ID: $userId and imagee ID: $imageId not found.`

OR 

**Code:** `NOT FOUND` \
**Content:** `Image with ID: $imageId not found.`

OR

**Code:** `FORBIDDEN` \
**Content:** `No permission to access image with ID: $imageId.`