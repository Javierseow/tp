# User Guide

## Introduction

FitLogger is a lightning-fast Command Line Interface (CLI) fitness tracker designed specifically for hybrid athletes. Whether you are hitting the gym for a heavy lifting session or hitting the pavement for a tempo run, FitLogger lets you record your workouts in seconds without ever taking your hands off the keyboard.

## Quick Start

1. Ensure you have Java `17` or above installed on your computer.
2. Download the latest `[CS2113-F09-1][FitLogger].jar` file from our [Releases](https://github.com/dinvsh/tp/releases) page.
3. Copy the file to the folder where you want to store your fitness data.
4. Open a command terminal (e.g., Command Prompt, PowerShell, or Terminal), navigate to the folder, and run the application using the command: `java -jar [CS2113-F09-1][FitLogger].jar`
5. Type `help` to see the list of available commands and start logging!

## Features

### Exercise shortcut database: `view-database`

Displays all available exercise shortcuts and their IDs, for both lift and run categories.

Format: `view-database`

- Shortcuts are listed separately for **Strength Shortcuts** and **Run Shortcuts**.
- The database comes pre-loaded with common exercises.
- Custom shortcuts you have added with `add-shortcut` also appear here.
- Ignores all trailing inputs

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

### Viewing detailed database: `view-detailed-database`

Displays all exercise shortcuts, their IDs, and their associated muscle groups in one view.

Format: `view-detailed-database`

- Similar to 'view-database' but with associated muscle groups for lift shortcuts shown
- Ignores all trailing inputs

Example Output:
```
Strength Shortcuts:
  [1] -> Squat (Muscles: glutes, quads, hamstring)
  [2] -> Bench Press (Muscles: delts, pecs, triceps)
  [3] -> Deadlift (Muscles: lower back, traps, glutes, hamstring)
  [4] -> Overhead Press (Muscles: delts, triceps, traps)

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
- **Note:** If you overwrite an existing lift shortcut ID, any muscle groups previously tagged to that ID will be automatically reset.

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

### Deleting a custom shortcut: `delete-shortcut`

Removes a custom exercise shortcut from the database.

Format: `delete-shortcut <lift/run> <ID>`

- `<lift/run>` — must be exactly `lift` or `run`.
- `<ID>` — the numeric ID of the shortcut you want to delete.
- **Note:** Deleting a lift shortcut will also automatically remove any muscle group tags associated with it.

Examples:
- `delete-shortcut lift 5`
- `delete-shortcut run 4`

Expected output:

`Success! Removed strength shortcut: [L5] -> Romanian Deadlift`

---

### Logging a strength workout: `add-lift`

Logs a strength workout. You can identify the exercise by full name or by shortcut ID from the database.

Format: `add-lift <NAME_OR_ID> w/<weightKg> s/<sets> r/<reps>`

- `<NAME_OR_ID>` — the full exercise name (e.g. `Bench Press`) or a numeric shortcut ID (e.g. `2`). Use `view-database` to see available IDs.
- `w/` — weight in kilograms. Use `0` for bodyweight exercises.
- `s/` — number of sets (positive integer).
- `r/` — reps per set (positive integer).
- The flags `w/`, `s/`, `r/` must appear in that order.
- `weight` must use normal decimal notation such as `80` or `80.5`; scientific notation such as `8e1` is rejected.
- `sets` and `reps` must be whole numbers from 1 to 1,000,000.
- **Warning:** The exercise name cannot contain the pipe `|` or forward slash `/` characters, as these are reserved for saving your data.

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
- Distance and duration must use normal decimal notation such as `5` or `5.0`; scientific notation such as `5e0` is rejected.

Examples:

- `add-run Tempo Run d/5.0 t/25`
- `add-run 2 d/5.0 t/25` _(shortcut ID 2 → Tempo Run)_
- `add-run Easy Run d/3.0 t/20.5` _(fractional duration)_

Expected output:

```
Got it. I've added this workout:
[Run] Tempo Run (Date: 2026-04-03) (Distance: 5.00km, Duration: 25.00 mins)
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
`Updated workout 2: [Run] Easy Run (Date: 2026-04-02) (Distance: 3.00km, Duration: 3.00 mins)`

Editable fields:
- For all workouts: `name`
- For run workouts: `distance`, `duration`
- For lift workouts: `weight`, `sets`, `reps`

Important:
- Use full field names in `edit` commands.
- Shorthand flags such as `d/` and `t/` are for `add-run`, not `edit`.
- For example, use `edit 1 distance/5` instead of `edit 1 d/5`.
- Decimal fields (`weight`, `distance`, `duration`) must use normal decimal notation, not `NaN`, `Infinity`, or scientific notation.
- Integer fields (`sets`, `reps`) must be whole numbers from 1 to 1,000,000.

Invalid input example, assuming workout 2 is a lift workout:
`edit 2 weight/abc`

Expected error:
`Invalid weight value: abc`

---

### Delete a workout: `delete`

Deletes one workout by index.

Format: `delete <index>`

The command accepts exactly one index. Extra values such as `delete 1 2` are rejected.

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

Displays all workouts you have logged, in the order they were added. You can optionally specify a number to view only the most recent entries.

Format: `history [NUMBER]`

- Both run and strength workouts are listed together.
- Each workout is numbered for use with `edit` and `delete` (i.e. index shown corresponds to index of workout in the list)
- If `[NUMBER]` is provided, only that many of the most recent workouts are shown.
- If `[NUMBER]` is provided but exceeds entries available, full history is displayed
- If `[NUMBER]` is omitted, the full history is displayed.

Example: `history` Expected output:

```
Here's your past exercises:
-----------------------------------------------------
1. [Lift] Deadlift (Date: 2026-04-02) (80.0kg, 3 sets of 8 reps)
2. [Run] Interval Run (Date: 2026-04-03) (Distance: 5.00km, Duration: 25.00 mins)
-----------------------------------------------------
```

Example: `history 2` Expected output:

```
Showing the last 2 exercise(s):
-----------------------------------------------------
2. [Lift] Bench Press (Date: 2026-04-02) (80.0kg, 3 sets of 8 reps)
3. [Run] Tempo Run (Date: 2026-04-03) (Distance: 5.00km, Duration: 25.00 mins)
-----------------------------------------------------
```

- If no workouts have been logged yet, nothing will be listed between the lines.

---

### Search workouts by date: `search-date`

Shows workouts completed on the specified date.

Format: `search-date <YYYY-MM-DD>`

The command accepts exactly one date. Extra dates or words after the date are rejected.

Example:
- `search-date 2026-03-15`

Sample output when matches exist:
```
Workouts on 2026-03-15:
-----------------------------------------------------
1. [Run] Morning Run (Date: 2026-03-15) (Distance: 5.00km, Duration: 30.00 mins)
-----------------------------------------------------
```

Sample output when no matches exist:
`No workouts found.`

Invalid input example:
`search-date 2026/03/15`

Expected error:
`Invalid date format for search-date.`

---
### View workout calendar: `view-calendar`

Displays an text-based calendar for a specific month, highlighting the days you successfully logged a workout.

Format: `view-calendar YYYY-MM>`

If no date is provided (e.g., just view-calendar), it defaults to the current month.

Active days (days where you logged at least one workout) are highlighted with square brackets [ ].

This command is perfect for visualizing your training consistency and streaks.

Examples:

`view-calendar` (Shows the current month)

`view-calendar 2026-04` (Shows April 2026)

Sample output:
```
      APRIL 2026  

Su  Mo  Tu  We  Th  Fr  Sa

             1   2   3   4

 5   6   7  [8]  9  10  11

12  13  14  15  16 [17] 18

19  20  21  22  23  24  25

26  27  28  29  30
```

Invalid input example:

`view-calendar 2026/04`

Expected error:
`Invalid calendar format. Use YYYY-MM (e.g., view-calendar 2026-04)`

---
### Filter workouts by muscle group: `filter`

Shows only the workouts from your history that target a specific muscle group.

Format: `filter <MUSCLE_GROUP> [<MUSCLE_GROUP2> ...]`

- The `<MUSCLE_GROUP>` must be a valid group from the database (e.g., `pecs`, `delts`, `quads`, `upper_back`, `lower_back`).
- Multi-word muscle groups must use underscores (e.g., `upper_back` for "upper back", `lower_back` for "lower back").
- Supports filtering by multiple muscle groups (space-separated or comma-separated).
- Only **Strength Workouts** that have been tagged with the specified muscle group will be displayed.
- Run workouts are currently excluded from this filter.

Examples:
- `filter delts` — Shows workouts targeting delts
- `filter upper_back` — Shows workouts targeting upper back
- `filter pecs triceps` — Shows workouts targeting either pecs or triceps
- `filter quads, hamstring, glutes` — Shows workouts targeting any of these muscle groups

Sample output when matches exist:
`Workouts matching category [upper back]:`
`1. Deadlift (Date: 2026-04-03) (100.0kg, 3 sets of 5 reps)`

Sample output when no matches exist:
`No workouts found.`

Invalid input example:
`filter`

Expected error:
`Please specify a muscle group. Usage: filter <muscle_group> [<muscle_group2> ...]`

> Tip: Use `view-muscle-groups` to see the full list of available categories you can use with this command. Multi-word groups like "upper back" should be typed as `upper_back` with an underscore.

---

### Viewing total mileage: `view-total-mileage`

Displays the total distance you have run. You can optionally specify a number of days to see mileage within a specific timeframe.

Format: `view-total-mileage [DAYS]`

- `[DAYS]` — a non-negative integer. Filters distance to runs within the last X days, excluding the current day (i.e. if `DAYS` = 1, mileage shown will include yesterday and today's runs)
- If `[DAYS]` is omitted, the total all-time mileage is displayed.
- Distance is formatted to 2 decimal places to avoid scientific notation.

Example: `view-total-mileage 7` Expected output:

```
Total shoe mileage (past 7 day(s)): 15.50km across 3 run(s).
```

Example: `view-total-mileage 0` Expected output:
```
Total shoe mileage (today): 4.00km across 1 run(s).
```

Example: `view-total-mileage` Expected output:
```
Total shoe mileage (all time): 42.00km across 5 run(s).
```

---

### Setting your profile: `profile set`

Updates one field of your profile.

Format: `profile set <field> <value>`

- `<field>` — must be one of `name`, `height`, or `weight`.
- For `height`, provide a value in metres (e.g. `1.75`). Accepted range: `0.3` to `3`.
- For `weight`, provide a value in kilograms (e.g. `70`). Accepted range: `10` to `500`.
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

Expected error: 
```
Please provide a valid number for height/weight
```

Invalid input example: `profile set height 0.1`

Expected error: 
```
Your Height/Weight is unrealistically low/high.
Please ensure your values are correct, height in m and weight in kg
```

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
- This command ignores all trailing arguments

---

### Clearing your profile: `profile clear`

Resets all fields in your user profile (name, height, and weight) to their default "unset" state.

Format: `profile clear`

- Reverts name to `null` and numerical values to the sentinel `-1`.
- This change is reflected in the save file upon exit.
- Ignores all trailing arguments after clear

Expected output:
```
User profile has been cleared.
```

---

### View all muscle groups: `view-muscle-groups`

Displays all available muscle groups that can be used for tagging lift exercises and filtering workouts.

Format: `view-muscle-groups`

- Useful as a reference before using `tag-muscle`, `untag-muscle`, `train`, or `filter`.
- Ignores all arguments passed after the command.

Expected output:

```
Here are all available muscle groups: 
delts, pecs, forearms, upper back, lower back, abs, lats, biceps, triceps, traps, glutes, quads, hamstring, calves
```

---

### View muscle groups for an exercise: `muscle-groups`

Displays all muscle groups currently tagged to a specific lift shortcut.

Format: `muscle-groups <SHORTCUT_ID>`

- `<SHORTCUT_ID>` — the numeric ID of a **lift** shortcut from the database. Use `view-database` to see available IDs.
- Only works for **lift** shortcuts, not run shortcuts.

Examples:

- `muscle-groups 2`

Expected output when tags exist:

```
Muscle groups for Bench Press (ID: 2): delts, pecs, triceps
```

Expected output when no tags exist:

```
No muscle groups tagged for Bench Press (ID: 2).
```

---

### Tag a muscle group to an exercise: `tag-muscle`

Adds a muscle group tag to a lift shortcut in the database. This lets you use `train` and `filter` to find exercises targeting that muscle.

Format: `tag-muscle <SHORTCUT_ID> <MUSCLE_GROUP>`

- `<SHORTCUT_ID>` — the numeric ID of a lift shortcut. Use `view-database` to see available IDs.
- `<MUSCLE_GROUP>` — a valid muscle group name. Use `view-muscle-groups` to see available options.
- Multi-word muscle groups use a space (e.g., `upper back`).

Examples:

- `tag-muscle 1 quads`
- `tag-muscle 2 upper back`

Expected output:

```
Added quads to lift 1
```
If quads already tagged to lift 1, Expected output is:
```
quads is already tagged to lift 1
```
---

### Remove a muscle group tag from an exercise: `untag-muscle`

Removes a muscle group tag from a lift shortcut.

Format: `untag-muscle <SHORTCUT_ID> <MUSCLE_GROUP>`

- `<SHORTCUT_ID>` — the numeric ID of a lift shortcut. Use `view-database` to see available IDs.
- `<MUSCLE_GROUP>` — a valid muscle group currently tagged to that shortcut.

Examples:

- `untag-muscle 2 delts`

Expected output:

```
Removed delts from lift ID: 2
```
If delts is not originally tagged to lift 2, Expected output is:
```
delts was not found on lift ID: 2
```

---

### Find exercises targeting a muscle: `train`

Lists all lift shortcuts in the database that are tagged with the specified muscle group. Useful for planning your workout session around a target muscle.

Format: `train <MUSCLE_GROUP>`

- `<MUSCLE_GROUP>` — a valid muscle group name. Use `view-muscle-groups` to see all options.
- Multi-word muscle groups use a space (e.g., `train lower back`).
- Only **lift** shortcuts are searched.

Examples:

- `train delts`
- `train lower back`

Expected output when matches exist:

```
Exercises targeting: delts
   [2] -> Bench Press
   [4] -> Overhead Press
```

Expected output when no matches exist:

```
Exercises targeting: delts
No lift exercises currently targeting delts
Use 'tag-muscle <shortcut-ID> <muscle>' to tag an exercise
```

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

The command does not take any arguments. Extra words such as `exit now` are rejected.

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
- **Integer Limits:** Workout indexes, shortcut IDs, sets, and reps must be positive integers from 1 to 1,000,000.
- **Decimal Format:** Weight, distance, and duration must use ordinary decimal notation such as `80`, `80.5`, or `5.0`. Scientific notation, `NaN`, and `Infinity` are rejected.
- **Missing Flags:** If you forget a mandatory flag (like `w/` in `add-lift`), FitLogger will identify the missing component and show you the correct usage.
- **Type Mismatches:** If you enter text where a number is expected (e.g., `weight/abc`), you will receive a specific error such as `Invalid weight value: abc`.
- **Logic Bounds:** The parser prevents "impossible" data. For example, setting a height of `0.1m` or a weight of `1000kg` will trigger a warning to ensure your profile remains accurate.

#### Logging
FitLogger keeps command-level diagnostic logs in `logs/fitlogger.log` for troubleshooting. These Java logger messages are not printed in the command window during normal use, so user-facing output stays focused on command results and errors.

Storage-related save-file warnings are handled separately by the `Storage` component. For example, if `data/fitlogger.txt` is manually corrupted, inaccessible, or blocked by the operating system during save/load, Storage may print a warning about the affected save file in addition to the command-level error message.

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

| Action                     | Command Format                                   | Example                             |
|----------------------------|--------------------------------------------------|-------------------------------------|
| **Help**                   | `help`                                           | `help`                              |
| **View Profile**           | `profile view`                                   | `profile view`                      |
| **Clear Profile**          | `profile clear`                                  | `profile clear`                     |
| **Set Profile**            | `profile set <field> <value>`                    | `profile set weight 75`             |
| **Add Run**                | `add-run <NAME_OR_ID> d/<dist> t/<mins>`         | `add-run Tempo Run d/5.0 t/25`      |
| **Add Lift**               | `add-lift <NAME_OR_ID> w/<kg> s/<sets> r/<reps>` | `add-lift Bench Press w/80 s/3 r/8` |
| **Edit Workout**           | `edit <index> <field>/<value>`                   | `edit 1 weight/85`                  |
| **View Database**          | `view-database`                                  | `view-database`                     |
| **View Detailed Database** | `view-detailed-database`                         | `view-detailed-database`            |
| **Add Shortcut**           | `add-shortcut <lift/run> <ID> <name>`            | `add-shortcut lift 5 Muscle Up`     |
| **Delete Shortcut**        | `delete-shortcut <lift/run> <ID>`                | `delete-shortcut lift 5`            |
| **Total Mileage**          | `view-total-mileage [DAYS]`                      | `view-total-mileage 7`              |
| **Last Lift**              | `lastlift <EXERCISE_NAME>`                       | `lastlift Bench Press`              |
| **Last Cardio**            | `lastcardio <EXERCISE_NAME>`                     | `lastcardio Easy Run`               |
| **Personal Record**        | `pr <EXERCISE_NAME>`                             | `pr Bench Press`                    |
| **View Muscle Groups**     | `view-muscle-groups`                             | `view-muscle-groups`                |
| **Exercise Muscles**       | `muscle-groups <ID>`                             | `muscle-groups 2`                   |
| **Tag Muscle**             | `tag-muscle <ID> <muscle>`                       | `tag-muscle 1 quads`                |
| **Untag Muscle**           | `untag-muscle <ID> <muscle>`                     | `untag-muscle 1 quads`              |
| **Train Muscles**          | `train <muscle>`                                 | `train glutes`                      |
| **History**                | `history [NUMBER]`                               | `history 5`                         |
| **Filter Workout**         | `filter <MUSCLE_GROUP> [<MUSCLE_GROUP2> ...]`  | `filter upper_back`                 |
| **Delete Workout**         | `delete <index>`                                 | `delete 2`                          |
| **Search by Date**         | `search-date <YYYY-MM-DD>`                       | `search-date 2026-03-15`            |
| **View Calendar**          | `view-calendar <YYYY-MM>`                        | `view-calendar 2026-04`             |
| **Exit**                   | `exit`                                           | `exit`                              |
