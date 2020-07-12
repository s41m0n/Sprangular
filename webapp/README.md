# Ai20Lab05

Author: Simone Magnani (s265171)

Subject: Internet Applications - Laboratory n°5

**DISCLAIMER**

I tried to dig deeper the proposed arguments and solution to develop a version more similar to the final project. In fact, I've used:

* Router and nested routes (children)
* User privileges
* Course entities (not hardcoded) and an apposite useful service
* Interceptors and guards
* Different directory hierarchy to match the final ideal one
* Advance signaling mechanism (Subject/BehaviourSubject)
* Toastr notification (as in Android)
* A personal BroadcasterService to announce other component (ancestors not directly connected, since subroutes) of many event such as
    * Course loaded => then the AppComponent can retrieve it and display the name
    * Course loaded => then the nephews can retrieve it and get info about it like the enrolled students et, .
    * EntityNotFound => then the AppComponent can switch view from the current tab to a dynamic NotFound page
* Dynamic role-routes binding (for the sidenav)

I hope my effort would be positively considered, since I really liked those arguments and I wanted to prove not only that I've understood them, but also that with a bit more effort I was able to achieve a (in my opinion) notable result.s

## Compile and run

Install all the dependencies running `npm install`.

You should now be ready to run the application both in development and production mode.

### JSON-server

The site needs a running backend to retrieve all the resources (students, courses, ...) which will mapped, in development mode, to one internal route `/api` thanks to a proxy. If you have installed `json-server-auth` globally, you can just type `json-server-auth virtuallabs.json -r virtuallabs_routes.json` from the root of the project, otherwise run `node_modules/json-server-auth/dist/bin.js virtuallabs.json -r virtuallabs_routes.json` to take it from the node modules directory (launched always from the root).

### Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

### HTTPS

Run `ng serve --ssl` to start the dev server in Https mode, enabling encryptions for the entire website.


### Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

Rung `ng serve --open` to host the server according to the produced files in the `dist/` directory.

## Application state

This behaviour refers to the actual state of the project. There are 5 students in the `json-server-auth` database listed in [this](./virtuallabs.json) configuration file.

There site is a Single Page Application, in which you can login and navigate through all your courses.

There is one default user with `Professor` privileges who can authenticate into the system and it is listed in the same configuration file of before:

* email: olivier@mail.com  
* password: bestPassw0rd

When logged, the user can navigate though all the courses he belongs, but right now only the tab `students` shows something, while the other two will be implemented later in the final project.

Since it is a professor, he can both enroll and unenroll students from the courses. For the sake of simplicity in json-server and as required by the professor, the relationships between entities are simplified. This means that a professor does not have a list of its courses and a student can be enrolled only to 1 course (and belong to 1 team) at a time. The team functionality is not implemented yet.

The sidenav is shown by default.

## Expected behaviour

### Login

When logging in, I expect that if the credentials are correct the user is successfully authenticated and he can then navigate though all its allowed views. Moreover, If he had required a page which was restricted by authentication, after the login he will be automatically redirected to that page.

### After Login

After a successfully login, I expect the sidenav to be filled with all available courses from the database.

### Sidenav course click

When clicking on a course from the sidenav, I expect the course homepage is displayed with the three navigation tabs (students/vms/assignments).

### Students tab click

When clicking students tab inside a course, I expect that the table with the enrolled students is displayed and the user can now enroll/unenroll students.
All the other tabs display a simple phrase to show that they work, but are empty by now.

### PageNotFound

When navigating though a non-existent page, I expect the PageNotFound view is diplayed, notifying the user of the error. Moreover, if this error comes from a navigation resource not found (example /professor/courses/XXXX where XXXX does not exist), an additional error message is displayed (by now) and the students tab will hide.

### Manually URL insertion

If the user decides not to navigate using links but by manually modifying the URL, I expect the same default behaviour. If the resource exists and the user has the rights to interact with, then the requested page (dynamically and asynchronously loaded) will be displayed, otherwise the NotFound page will appear.

### Page Refresh

When refreshing the page, I expect that, if the auth token is still valid, the view displayed to the user is the same as before. Moreover, thanks to my advances routing navigation system and signaling, all the error are caught, including the ones of the subroutes. Thus, there is no way that a page loads if it has previously failed, the final state will be the same.

### Autocomplete options

By clicking on the input next to the "Add" button, I expect that all the students in the remote database are shown as suggestions. These students will be displayed with only few of their properties, while the whole structure is the real data used among these components.

By start typing/removing letters I expect that only the options which includes those letters are displayed, while the others are pruned.

By clicking one of the proposed options I expect that it is inserted in the input, showing only the student name.

If the input contains spaces before or at the end of the string, those will be trimmed and the suggestion should appear the same, while if it contains whitespaces in the middle, no suggestions will be displayed, since right now we only check for students surname (I could split the string and take only the first part, but that would lead to inconsistencies that I want to avoid for a better usage).

### Remove student from the table

By selecting a single checkbox and clicking on the "Delete" button, I should see the row removed from the table and a Toastr appearing showing a result message.

By selecting a multiple checkboxes and clicking on the "Delete" button, I should see the rows removed from the table and a Toastr appearing showing a result message.

By selecting the master checkbox and clicking on the "Delete" button, I should see all the removed from the table and a Toastr appearing showing a result message.

### Add student to the table

All the following actions are performed once an autocomplete option has been selected and I click the "Add" button.

If the user is already in the course/table, I expect that it is not inserted twice (but since put is idempotent the operation will not fail) and a message showing the result is displayed.

If the user was not in the course/table yet, I expect that it is inserted as a new row and a message showing the result is displayed.

If current field in the input is actually not a student, I expect that nothing alters the system and a message showing the result is displayed.

### Paging

Once the application has started, I expect that the pager shows that there are 5/5 entries, all shown in the first page.

As soon as a student is added, I expect the number of entries changes but the new inserted row would not be shown if the current page contains enough entries.

As soon as a student is deleted, I expect the number of entries changes and if there were entries in other pages, one of them is displayed in the current one.

### Sorting

By clicking on one of the table header referring to the user informations (so not the checkboxes one), I expect the rows will be ordered according to the current method (Ascending/Descending comparing their values with the default methodology).

### Menu icon

By clicking the menu icon, I expect the sidenav to open, showing all the available courses list.

### Title

When clicking on `VirtualLabs` title, I expect the user is redirect to the site homepage (and not the course homepage).



## Screenshots

Available at `screenshots` directory.
