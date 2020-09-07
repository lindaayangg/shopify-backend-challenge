## Delete Images by User

### Description
Delete images selectively or all by user ID.

### Endpoint
`/images`

### Method
`DELETE`

### URL Parameters
**Delete all images that belong to the specified user:**\
`userId = 1`

OR

**Delete images selectively by specifying their IDs:**\
`userId = 1`\
`imageIds = 1, 2`

### Data Parameters
`None`

### Success Response
**Delete all images that belong to the specified user:**\
**Code:** `200 OK` \
**Content:** `All images for user with ID: $userId deleted successfully!`

OR 

**Delete images selectively by specifying their IDs:**\
**Code:** `200 OK` \
**Content:** `Images deleted successfully!`

### Error Response
**Delete all images that belong to the specified user:**\
**Code:** `NOT FOUND` \
**Content:** `User with ID: $userId not found.`

OR

**Code:** `INTERNAL SERVER ERROR` \
**Content:** `Failed to delete images for user with ID: $userId.`

--

**Delete images selectively by specifying their IDs:**\
**Code:** `NOT FOUND` \
**Content:** `Image with ID: $imageId not found.`

OR

**Code:** `FORBIDDEN` \
**Content:** `No permission to delete image with ID: $imageId.`

OR

**Code:** `INTERNAL SERVER ERROR` \
**Content:** `Failed to delete image with ID: $imageId.`


