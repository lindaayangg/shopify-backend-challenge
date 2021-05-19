## Set Image Information

### Description
Set image's information, including the price, discount, and amount in inventory.

### Endpoint
`/images/{imageId}`

### Method
`PATCH`

### URL Parameters
`userId = 1`

### Data Parameters
`price = 9.99`\
`discount = 20`\
`amount = 5`

### Success Response
**Code:** `200 OK` \
**Content:**
```
Price for image with ID: $imageId + updated to $price.
Discount for image with ID: $imageId + updated to $discount.
Inventory for image with ID: $imageId + updated to $amount.
```

### Error Response
**Code:** `NOT FOUND` \
**Content:** `Image with ID: $imageId not found.`

OR

**Code:** `FORBIDDEN` \
**Content:** `No permission to modify image with ID: $imageId.`