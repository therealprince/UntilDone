<div align="center">

<br/>

# ◎ UntilDone

### *Great plans deserve relentless execution.*

**A local-first Android app for people who are tired of planning and ready to actually do the thing.**

<br/>

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose&logoColor=white)
![No Cloud](https://img.shields.io/badge/No%20Cloud-No%20Ads-black?style=flat)
![License: MIT](https://img.shields.io/badge/License-MIT-green.svg?style=flat)

<br/>

</div>

---

## The honest pitch

Most productivity apps want you to *organize* your life. Color-code your goals. Build systems. Set up dashboards. You spend 45 minutes planning your week and feel weirdly productive without doing a single real thing.

**UntilDone is different.** It's built around one idea — you pick something you want to finish, you set a target, and you just… go. The app gets out of your way. No subscriptions. No syncing to the cloud. No motivational quotes at 9am. Just you and the thing you said you'd do.

---

## What it does

**Missions** — Call them goals, projects, habits, whatever. You create one, set a target (90 sessions of guitar practice, 12 books, 100 hours of code), and start chipping away at it. Every day you log progress. That's it.

**Focus Timer** — A clean 25-minute deep work timer. Start it when you sit down to work. It logs your session automatically. No fuss.

**Missions Board** — See everything at once: what's active, what's done. When a mission hits 100%, it moves to Completed. No manual archiving.

**Insights** — A minimal analytics view. Your streak, execution points, weekly consistency. Just enough to know if you're showing up.

**Your data, your phone** — Everything is stored locally in SQLite. Nothing leaves your device unless *you* create a backup. You can schedule automatic backups or do it manually. Restore anytime.

---

## Designed to feel good

UntilDone follows your system's dark/light mode automatically. The UI is clean, minimal, and fast — no loading spinners, no skeleton screens, no unnecessary animations. It feels like an app someone actually thought about.

- Dark mode that's actually dark
- Adaptive icon that looks right on any launcher
- Smooth screen transitions
- Bottom nav that makes sense

---

## Privacy, for real

There's no backend. No account recovery email. No analytics SDK phoning home. No third-party anything.

You sign up with an email and password stored locally. Your password is hashed (SHA-256). Your data lives in a SQLite database on your phone. If you uninstall the app, everything's gone — unless you made a backup.

This is the trade-off. We think it's worth it.

---

## Get started

**Requirements:** Android 7.0+ (API 24)

1. Clone the repo
2. Open in Android Studio
3. Hit run

```bash
git clone https://github.com/yourusername/UntilDone.git
```

No API keys. No Firebase setup. No `.env` file. Just open and run.

---

## Built with

- **Kotlin** + **Jetpack Compose** — native Android UI
- **SQLite** via `SQLiteOpenHelper` — no ORM, no Room, no magic
- **Gson** — for backup serialization
- **AlarmManager** — for scheduled backups
- **Nothing else** — seriously

---

## Contributing

Found a bug? Have an idea that fits the vibe? PRs are welcome. Keep it minimal. If the change makes the app more complex without meaningfully helping the person using it, it probably doesn't belong here.

---

## License

MIT — do whatever you want with it.

---

<div align="center">

<br/>

*Great Plan Deserve Relentless Execution.*

**◎**

<br/>

</div>
