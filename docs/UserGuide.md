# User Guide

## Introduction

FitLogger is a lightning-fast Command Line Interface (CLI) fitness tracker designed specifically for hybrid athletes. Whether you are hitting the gym for a heavy lifting session or hitting the pavement for a tempo run, FitLogger lets you record your workouts in seconds without ever taking your hands off the keyboard.

## Quick Start

1. Ensure you have Java `17` or above installed on your computer.
2. Download the latest `fitlogger.jar` file from our [Releases](https://github.com/dinvsh/tp/releases) page.
3. Copy the file to the folder where you want to store your fitness data.
4. Open a command terminal (e.g., Command Prompt, PowerShell, or Terminal), navigate to the folder, and run the application using the command: `java -jar fitlogger.jar`
5. Type `help` to see the list of available commands and start logging!

## Features 

### Exercise shortcut database: `view-database`

Displays all available exercise shortcuts and their IDs, for both lift and run categories.

Format: `view-database`

- Shortcuts are listed separately for **Strength Shortcuts** and **Run Shortcuts**.
- The database comes pre-loaded with common exercises.
- Custom shortcuts you have added with `add-shortcut` also appear here.

Example output:
```
Strength Shortcuts:
  [1] -> Squat
  [2] -> Bench Press
  [3] -> Deadlift
  [4] -> Overhead Press
 
Run Shortcuts:
  [1] -> Easy Run
  [2] -> Tempo Run
  [3] -> Intervals
```
 
---

### Adding a custom shortcut: `add-shortcut`

Adds a custom exercise shortcut to the database, assigning it a numeric ID you can use in `add-lift` or `add-run` instead of typing the full name each time.

Format: `add-shortcut <lift/run> <ID> <EXERCISE_NAME>`

- `<lift/run>` — must be exactly `lift` or `run`.
- `<ID>` — a positive integer. If the ID already exists, it overwrites the existing shortcut.
- `<EXERCISE_NAME>` — must not contain `|` or `/`.

Examples:
- `add-shortcut lift 5 Romanian Deadlift`
- `add-shortcut run 4 Hill Repeats`

Expected output:
```
Success! Added strength shortcut: [L5] -> Romanian Deadlift
```

You can then log using the ID directly instead of the full name:
```
add-lift 5 w/60 s/3 r/10
```
 
---

### Logging a strength workout: `add-lift`

Logs a strength workout. You can identify the exercise by full name or by shortcut ID from the database.

Format: `add-lift <NAME_OR_ID> w/<weightKg> s/<sets> r/<reps>`

- `<NAME_OR_ID>` — the full exercise name (e.g. `Bench Press`) or a numeric shortcut ID (e.g. `2`). Use `view-database` to see available IDs.
- `w/` — weight in kilograms. Use `0` for bodyweight exercises.
- `s/` — number of sets (positive integer).
- `r/` — reps per set (positive integer).
- The flags `w/`, `s/`, `r/` must appear in that order.

Examples:
- `add-lift Bench Press w/80 s/3 r/8`
- `add-lift 2 w/80 s/3 r/8` *(shortcut ID 2 → Bench Press)*
- `add-lift Pull-up w/0 s/4 r/12` *(bodyweight)*

Expected output:
```
Got it. I've added this workout:
[Lift] Bench Press (Date: 2026-04-02) (80.0kg, 3 sets of 8 reps)
Now you have 3 workouts in the list.
```

> You can correct any field later using `edit <index> <field>/<value>`. Valid fields for lifts: `name`, `weight`, `sets`, `reps`.

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## Command Summary

| Action | Command Format | Example |
|---|---|---|
| **Help** | `help` | `help` |
| **Add Lift** | `add-lift <NAME_OR_ID> w/<kg> s/<sets> r/<reps>` | `add-lift Bench Press w/80 s/3 r/8` |
| **Add Run** | `add-run <NAME_OR_ID> d/<dist> t/<mins>` | `add-run Tempo Run d/5.0 t/25` |
| **View Database** | `view-database` | `view-database` |
| **Add Shortcut** | `add-shortcut <lift/run> <ID> <name>` | `add-shortcut lift 5 Muscle Up` |
| **Edit Workout** | `edit <index> <field>/<value>` | `edit 1 weight/85` |
| **Delete Workout** | `delete <index_or_name>` | `delete 2` OR `delete Tempo Run` |
| **History** | `history` | `history` |
| **View Profile** | `profile view` | `profile view` |
| **Set Profile** | `profile set <field> <value>` | `profile set weight 75` |
| **Total Mileage** | `view-total-mileage` | `view-total-mileage` |
| **Exit** | `exit` | `exit` |

* Add todo `todo n/TODO_NAME d/DEADLINE`
