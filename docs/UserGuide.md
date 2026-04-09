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

---

### Logging a run workout: `add-run`

Logs a run workout. You can identify the run type by full name or by shortcut ID from the database.

Format: `add-run <NAME_OR_ID> d/<distanceKm> t/<durationMinutes>`

- `<NAME_OR_ID>` — the full run name (e.g. `Tempo Run`) or a numeric shortcut ID (e.g. `2`). Use `view-database` to see available IDs.
- `d/` — distance in kilometres (positive number).
- `t/` — duration in minutes (positive number, decimals allowed e.g. `25.5`).
- The flags `d/`, `t/` must appear in that order.

Examples:

- `add-run Tempo Run d/5.0 t/25`
- `add-run 2 d/5.0 t/25` _(shortcut ID 2 → Tempo Run)_
- `add-run Easy Run d/3.0 t/20.5` _(fractional duration)_

Expected output:

```
Got it. I've added this workout:
[Run] Tempo Run (Date: 2026-04-03) (Distance: 5.0km, Duration: 25.0 mins)
Now you have 4 workouts in the list.
```

> You can correct any field later using `edit <index> <field>/<value>`. Valid fields for runs: `name`, `distance`, `duration`.

---

### Edit an existing workout: `edit`

Updates one field of an existing workout by index.

Format: `edit <index> <field>/<value>`

Examples:
- `edit 1 distance/4.7`
- `edit 2 reps/10`
- `edit 3 name/Tempo Run`

Sample output:
`Updated workout 2: [Run] Easy Run (Date: 2026-04-02) (Distance: 3.0km, Duration: 3.0 mins)`

Editable fields:
- For all workouts: `name`, `description`
- For run workouts: `distance`, `duration`
- For lift workouts: `weight`, `sets`, `reps`

Important:
- Use full field names in `edit` commands.
- Shorthand flags such as `d/` and `t/` are for `add-run`, not `edit`.
- For example, use `edit 1 distance/5` instead of `edit 1 d/5`.

Invalid input example:
`edit 1 weight/abc`

Expected error:
`Invalid weight value: abc`

---

### Delete a workout: `delete`

Deletes one workout by index.

Format: `delete <index>`

Examples:
- `delete 1`
- `delete 3`

Sample output:
`Deleted workout: Bench Press`

Invalid input example:
`delete abc`

Expected error:
`Workout index must be a positive integer.`

---

### Viewing workout history: `history`

Displays all workouts you have logged, in the order they were added.

Format: `history`

- Both run and strength workouts are listed together.
- Each workout is numbered for use with `edit` and `delete`.

Expected output:

```
Here's your past exercises
-----------------------------------------------------
1. [Lift] Bench Press (Date: 2026-04-02) (80.0kg, 3 sets of 8 reps)
2. [Run] Tempo Run (Date: 2026-04-03) (Distance: 5.0km, Duration: 25.0 mins)
-----------------------------------------------------
```

- If no workouts have been logged yet, nothing will be listed between the lines.

---

### Search workouts by date: `search-date`

Shows workouts completed on the specified date.

Format: `search-date <YYYY-MM-DD>`

Example:
- `search-date 2026-03-15`

Sample output when matches exist:
`Workouts on 2026-03-15:`

Sample output when no matches exist:
`No workouts found.`

Invalid input example:
`search-date 2026/03/15`

Expected error:
`Invalid date format for search-date.`

---

### Filter workouts by muscle group: `filter`

Shows only the workouts from your history that target a specific muscle group.

Format: `filter <MUSCLE_GROUP>`

- The `<MUSCLE_GROUP>` must be a valid group from the database (e.g., `pecs`, `delts`, `quads`).
- Only **Strength Workouts** that have been tagged with the specified muscle group will be displayed.
- Run workouts are currently excluded from this filter.

Example:
- `filter delts`

Sample output when matches exist:
`Workouts matching category [delts]:`
`1. Overhead Press (Date: 2026-04-03) (40.0kg, 3 sets of 8 reps)`

Sample output when no matches exist:
`No workouts found.`

Invalid input example:
`filter`

Expected error:
`Please specify a muscle group. Usage: filter <muscle_group>`

> Tip: Use `view-muscle-groups` to see the full list of available categories you can use with this command.

---

### Viewing total mileage: `view-total-mileage`

Displays the total distance you have run across all logged run workouts.

Format: `view-total-mileage`

Expected output:

```
Your total distance ran is 13.50km across 3 runs.
```

- Only `add-run` workouts are counted. Strength workouts are excluded.
- If no runs have been logged, the total will show `0.00km` across `0` runs.

---

### Setting your profile: `profile set`

Updates one field of your profile.

Format: `profile set <field> <value>`

- `<field>` — must be one of `name`, `height`, or `weight`.
- For `height`, provide a value in metres (e.g. `1.75`). Accepted range: `0.3m` to `3m`.
- For `weight`, provide a value in kilograms (e.g. `70`). Accepted range: `10kg` to `500kg`.
- For `name`, provide any text string.

Examples:

- `profile set name John`
- `profile set height 1.75`
- `profile set weight 70`

Expected output:

```
Height has been updated to 1.75m
```

Invalid input example: `profile set weight abc`

Expected error: `Please provide a valid number for height/weight`

Invalid input example: `profile set height 0.1`

Expected error: `Your Height/Weight is unrealistically low/high.` `Please ensure your values are correctly, height in m and weight in Kg`

---

### Viewing your profile: `profile view`

Displays your currently saved profile information, including name, height, and weight.

Format: `profile view`

Expected output:

```
-----------------------------------------------------
Name: John
Height: 1.75m
Weight: 70.00kg
-----------------------------------------------------
```

- If a field has not been set yet, it will display a placeholder (e.g., `name not set yet`).
- This command ignores all trailing inputs

---
### Viewing your last lift: `lastlift`

Displays the most recent recorded stats for a specific strength exercise.
Useful for quickly checking what weight, sets, and reps you last did before
heading to the gym.

Format: `lastlift <EXERCISE_NAME>`

Examples:
- `lastlift Bench Press`
- `lastlift Squat`

Expected output:
```
-----------------------------------------------------
Last recorded lift for: Bench Press
  Date   : 2026-03-21
  Weight : 80.0kg
  Sets   : 3
  Reps   : 8
-----------------------------------------------------
```

Notes:
- The exercise name is case-insensitive (`bench press` and `Bench Press` both work).
- If no matching exercise is found, you will see:
  `No record found for exercise: Bench Press`
- Only strength workouts are searched — run workouts are ignored.

---

### Viewing your personal record: `pr`

Displays your personal record for a specific exercise — the highest weight
ever lifted for a strength exercise, or the longest distance run for a run exercise.

Format: `pr <EXERCISE_NAME>`

Examples:
- `pr Bench Press`
- `pr Easy Run`

Expected output (strength):
```
-----------------------------------------------------
Personal Record for: Bench Press
  Date   : 2026-04-01
  Weight : 100.0kg
  Sets   : 3
  Reps   : 5
-----------------------------------------------------
```

Expected output (run):
```
-----------------------------------------------------
Personal Record for: Easy Run
  Date     : 2026-03-15
  Distance : 21.1km
  Duration : 120.0 mins
-----------------------------------------------------
```

Notes:
- The exercise name is case-insensitive.
- If no matching exercise is found, you will see:
  `No record found for exercise: Bench Press`
- For strength exercises, the PR is the entry with the **highest weight**.
- For run exercises, the PR is the entry with the **longest distance**.

---

### Saving and loading your data

FitLogger automatically saves and loads your data — you do not need to do
anything manually.

**Saving:**
- Happens automatically when you type `exit`.
- All workouts and your profile are written to `data/fitlogger.txt` in the
  same folder as the app.

**Loading:**
- Happens automatically when FitLogger starts up.
- Your previous workouts and profile are fully restored.
- If the file does not exist yet (first run), FitLogger starts with an empty
  list — this is expected and not an error.

**If the save file is corrupted:**
- FitLogger skips the corrupted line and continues loading the rest of your data.
- A warning is printed for each skipped line:
  `Warning: Skipping corrupted line 3 in save file: <line content>`
- Valid entries above and below the corrupted line are still loaded.

> **Note:** Do not manually edit `data/fitlogger.txt`. In particular, avoid
> using `|` or `/` characters — these are reserved by the storage format and
> will cause lines to be skipped on load.

### Exit the app: `exit`

Saves data and closes FitLogger.

Format: `exit`

--- 

### Getting help: `help`

If you are unsure of a command format or want to see all available features, use the help command.

Format: `help`

- Displays a comprehensive list of all commands.
- Provides the specific syntax/flags required for each command (e.g., `w/`, `s/`, `r/`).
- Useful for new users to quickly learn the "Hybrid Athlete" workflow.

---

### Understanding Error Messages and Validation

FitLogger includes an upgraded parser designed to catch common data entry mistakes before they save to your history.

#### Input Validation
When you enter data, the parser checks for the following:
- **Missing Flags:** If you forget a mandatory flag (like `w/` in `add-lift`), FitLogger will identify the missing component and show you the correct usage.
- **Type Mismatches:** If you enter text where a number is expected (e.g., `weight/abc`), you will receive a specific error: `Invalid number format`.
- **Logic Bounds:** The parser prevents "impossible" data. For example, setting a height of `0.1m` or a weight of `1000kg` will trigger a warning to ensure your profile remains accurate.

#### Error Handling Strategy
If a command fails, FitLogger will not crash. Instead, it will:
1. Provide a clear error message explaining *what* went wrong.
2. Show the **correct format** for that specific command.
3. Return you to the command prompt so you can try again immediately.

**Example of an Error Response:**
Input: `add-lift Bench w/heavy s/3`
Output:

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: Copy the `data/fitlogger.txt` file to the same relative location in the other machine.

**Q**: Why did `edit` fail even though the command format looked correct?

**A**: Check index bounds and field type compatibility. For `edit`, use full field names (e.g., `distance`, `duration`, `weight`). Short forms like `d/5` or `t/20` are not valid for `edit`.

## Command Summary

| Action | Command Format | Example |
|---|---|---|
| **Help** | `help` | `help` |
| **Add Lift** | `add-lift <NAME_OR_ID> w/<kg> s/<sets> r/<reps>` | `add-lift Bench Press w/80 s/3 r/8` |
| **Add Run** | `add-run <NAME_OR_ID> d/<dist> t/<mins>` | `add-run Tempo Run d/5.0 t/25` |
| **View Database** | `view-database` | `view-database` |
| **Add Shortcut** | `add-shortcut <lift/run> <ID> <name>` | `add-shortcut lift 5 Muscle Up` |
| **Edit Workout** | `edit <index> <field>/<value>` | `edit 1 weight/85` |
| **Delete Workout** | `delete <index>` | `delete 2` |
| **Search by Date** | `search-date <YYYY-MM-DD>` | `search-date 2026-03-15` |
| **History** | `history` | `history` |
| **View Profile** | `profile view` | `profile view` |
| **Set Profile** | `profile set <field> <value>` | `profile set weight 75` |
| **Total Mileage** | `view-total-mileage` | `view-total-mileage` |
| **Last Lift** | `lastlift <EXERCISE_NAME>` | `lastlift Bench Press` |
| **Personal Record** | `pr <EXERCISE_NAME>` | `pr Bench Press` |
| **Exit** | `exit` | `exit` |
