## Buy Image

### Description
Buy an image by ID.

### Endpoint
`/buy/{imageId}`

### Method
`POST`

### URL Parameters
`amount = 2`

### Data Parameters
`None`

### Success Response
**Code:** `200 OK` \
**Content:** `Purchased $amount images with image ID: $imageId for a total of $$total.`

### Error Response
**Code:** `NOT FOUND` \
**Content:** `Image with ID: $imageId not found.`

OR

**Code:** `NOT FOUND` \
**Content:** `Not enough inventory found for image with ID: $imageId.`