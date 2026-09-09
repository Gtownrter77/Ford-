# CB1 Chromebook Linux — operator handoff

**Date:** 2026-09-07 observed, filed 2026-09-09  
**Host:** cracked-screen 4 GB Chromebook, Crostini `penguin`, Debian trixie  
**Job:** 24/7 house Linux server. Pixel 8 is the remote screen.

ChromeOS must stay signed in, lid open, charger in, sleep **never** on AC. If ChromeOS sleeps, penguin dies.

## Storage

| Device | Last seen | Notes |
|---|---|---|
| `/` | virtio btrfs ~12.1G | Crostini root. Do not put models here. |
| `/dev/sdc1` | ext4 `SD_SRC` ~476.8G | UUID `70040cfb-73e4-4813-ab03-602daf79a901` → `/mnt/sd2` |
| Second SanDisk 512G | **not visible** in Crostini | ChromeOS Files / letter shift. Do not fake it with a bind. |

Mount options: `noatime,nodiratime,commit=120,errors=remount-ro,nofail,x-systemd.device-timeout=8`

## Memory

- Visible RAM ~2.7 GiB (Crostini share of 4 GiB).
- Swapfile `/mnt/sd2/swapfile` 8 GiB pri 10 — **confirmed active**.
- zram ram/2 lz4 installed via `systemd-zram-generator` — **timed out**. Reboot, then `sudo systemctl start /dev/zram0`.

`NODE_OPTIONS=--max-old-space-size=512` for any global npm.

## Software last seen

| Tool | State |
|---|---|
| Node | v22.23.2 NodeSource |
| Grok CLI | 1.0.13, `grok login --device-auth` as `rlongmbox@gmail.com` |
| OmniRoute | `sudo npm install -g omniroute` **in progress** 2026-09-07 ~02:38. Using SD swap. Do not claim healthy until `omniroute --version` and a local health check succeed. |
| OpenCode | not installed this shift |
| Tailscale | present in apt sources |

## After OmniRoute finishes

```
omniroute --version
# then a health check on the port the package actually binds
# historical note used :8080 — confirm, do not assume
```

Then OpenCode:

```
npm install -g opencode
```

Keep binaries and models on `/mnt/sd2`, not the 12G root.

## Do not

- Format a card ChromeOS still has open in Files (it will come back as exFAT).
- `fallocate` swap on FAT/exFAT (4 GiB file cap).
- Duplicate `/swapfile` lines in fstab (two stale internal-disk lines existed). Keep only the sd2 line plus zram generator.
