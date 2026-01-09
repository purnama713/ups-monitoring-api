# Device Log API Spec

## POST Device Log (For Microcontroller)

Endpoint: POST /api/devices/{deviceId}/statuses

- Request Body:
```json
{
  "id": "BIGINT, AUTO-INCREMENT",
  "battery_v": {
    "b0": 12.80,
    "b1": 12.80
  },
  "input_v": 220.1,
  "chargingState": "Charging",
  "powerState": "AC Line"
}
```
- Response Body (Success):
```json
{
  "data": "OK"
}
```

## GET Device Log

Endpoint: GET /api/devices/{deviceId}/statuses

- Response Body (Success):
```json
{
  "data": {
    "battery_v": {
      "b0": 12.80,
      "b1": 12.80
    },
    "input_v": 220.1,
    "chargingState": "Discharging",
    "powerState": "Battery",
    "timeStamp": "11:12:13 01-02-2003"
  }
}
```
- Response Body (Success, Device Offline):
```json
{
  "data": {
    "battery_v": {
      "b0": 12.80,
      "b1": 12.80
    },
    "input_v": 0,
    "chargingState": "Discharging",
    "powerState": "Power Off",
    "timeStamp": "11:12:13 01-02-2003"
  }
}
```
- Response Body (Success, Device Offline, Empty Battery):
```json
{
  "data": {
    "battery_v": {
      "b0": 9.80,
      "b1": 9.80
    },
    "input_v": 0,
    "chargingState": "Discharging",
    "powerState": "Battery Depleted",
    "timeStamp": "11:12:13 01-02-2003"
  }
}
```