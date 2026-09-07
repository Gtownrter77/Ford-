# OBD-II and FORScan Integration

## Implemented transport

The app now contains a real classic-Bluetooth ELM327 transport in `AndroidObd2BluetoothBridge`. It discovers **paired** Bluetooth adapters, requests Android 12+ Bluetooth permissions, opens the standard SPP UUID, initializes the adapter with `ATZ`, `ATE0`, `ATL0`, `ATS0`, and `ATSP0`, sends commands, reads the ELM327 `>` prompt, reports transport failures, and closes the socket when the dialog is dismissed.

The FORScan dialog no longer starts in a fabricated connected state. It starts disconnected, exposes a Connect action, uses paired adapter discovery, and only enters live transport state after a successful socket/ELM327 initialization. On a real connection it polls standard OBD-II Mode 01 PIDs for RPM (`010C`) and coolant temperature (`0105`). Unsupported or unavailable PIDs remain unclaimed rather than being presented as measured values.

## Implemented decoding

`Obd2PidParser` decodes the following standard responses:

| Command | PID | Value |
|---|---|---|
| `010C` | Engine RPM | `((A * 256) + B) / 4` rpm |
| `0105` | Engine coolant temperature | `A - 40` °C |
| `010B` | Intake manifold pressure | `A` kPa |
| `010D` | Vehicle speed | `A` km/h |
| `0111` | Throttle position | `A * 100 / 255` percent |
| `03` | Stored powertrain DTCs | SAE code list, including `P0171` formatting |

Transport responses containing `NO DATA`, `ERROR`, timeouts, or I/O failures are not treated as successful measurements.

## What this does not claim

This is an OBD-II transport and standard-PID layer, not a replacement for FORScan Desktop or an OEM Ford interface. It does not yet implement Ford proprietary module sessions, bidirectional controls, programming/configuration, service resets, security access, ABS/SRS/GEM/4WD proprietary polling, or actuator tests. Those functions require Ford-specific protocols, validated adapter compatibility, and a qualified operator.

The app cannot prove oil pressure, refrigerant pressure, timing-chain condition, or road safety through generic OBD-II. Mechanical gauges, A/C service equipment, scan-tool evidence, and physical inspection remain required.

## Safe use sequence

1. Pair a compatible ELM327/OBDLink-class Bluetooth adapter with the phone.
2. Turn the vehicle key to the required position; do not start or run the engine if the oil-pressure warning, overheating, smoke, active leak, or severe timing rattle makes operation unsafe.
3. Open **Diagnostics → Guided Flows & OBD → FORScan & OBD-II Scanner**.
4. Grant Bluetooth permissions and press **Connect**. The app uses the first paired adapter returned by Android.
5. Confirm that the status changes to the adapter name and **live transport** before treating RPM or coolant values as measured.
6. Use the DTC log workflow to parse codes and link known codes to the 3D model. A code is a diagnostic lead, not proof of a failed component.
7. Disconnect by closing the dialog. Never use unverified commands to change module configuration or operate an actuator.
