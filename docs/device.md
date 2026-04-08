# Device Log API Spec

## Register Device

- Endpoint : POST /api/devices

Request Header:

- X-API-TOKEN: Token (Mandatory)

Request Body :
```json
{
  "name": "Desktop UPS",
  "location": "Bedroom",
  "batteryCount": 2
}
```
Response Body (Success) :
```json
{
  "data" : {
    "deviceCode": "UPS-001-ddmmyyyy",
    "name": "Desktop UPS",
    "location": "Bedroom",
    "batteryCount": 2,
    "registeredAt": "11:12:13 01-02-2003"
  }
}
```
Response Body (Failed) :

```json
{
  "error" : "Name or battery count must not blank"
}
```

## Update Device Info

- Endpoint: PATCH /api/devices/{deviceId}

Request Header:

- X-API-TOKEN: Token (Mandatory)

Request Body:
```json
{
  "name": "Server UPS",
  "location": "Server Room"
}
```
Response Body (Success):
```json
{
  "data": {
    "name": "Server UPS",
    "location": "Server Room"
  }
}
```
Response Body (Failed):
```json
{
  "error": "Unauthorized"
}
```

## GET Device Info

Endpoint: GET /api/devices/{deviceId}

Response Body (Success):
```json
{
  "data": {
    "name": "Desktop UPS",
    "location": "Bedroom",
    "batteryCount": 2,
    "createdAt": "11:12:13 01-02-2003"
  }
}
```

## Unregister Device

Endpoint : DELETE /api/devices/{deviceId}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :
```json
{
  "data" : "OK"
}
```
Response Body (Failed) :
```json
{
  "error" : "Device not found"
}
```