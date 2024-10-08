# SpendWise

**SpendWise** is a budget management app that allows users to track their expenses, set budgets, and manage their finances efficiently. The app provides an intuitive interface and powerful features to help users stay on top of their spending.

## Technologies Used

**Java:** The primary programming language used to build the Android app.


**Android Studio:** The integrated development environment (IDE) for Android app development.

**Firebase Realtime Database:** For storing and syncing user data in real-time.

**Firebase Authentication:** For managing user sign-in and authentication.

**Firebase Storeage For Firebase** For saving each icon transaction

**Glide library:** For loading and caching images.


## Features

- **User Authentication**: Secure login and registration using Firebase Authentication.
  
- **Budget Management**: Set and track your budget with real-time updates.
  
- **Expense Tracking**: Add, view, and manage your transactions with ease.
  
- **Real-Time Sync**: Sync your data with Firebase, ensuring that your transactions and budget are always up to date.
  
- **Responsive UI**: User-friendly interface with support for different screen sizes.
  
- **Fragments Navigation**: Navigate easily between different sections of the app, including the budget manager and transaction history.

## Video Of SpendWise App:




https://github.com/user-attachments/assets/91cdd6e7-2a2c-4cc6-b6d4-00ecf1c2563a




## Screenshots

### Login And Set Budget:

<img src="https://github.com/user-attachments/assets/94ede773-ff9a-4baa-a2f0-5624d8b780ba" alt="login_spend_wise" width="300" height="600">   
    <img src="https://github.com/user-attachments/assets/b1583786-d82f-4d37-a156-fd09b037ce40" alt="set_budget_spend_wise" width="300" height="600"> 


### Manage Your Expenses:

<img src="https://github.com/user-attachments/assets/86eed1b4-7111-429e-b7c9-6cf4a8bd7cc3" alt="expnses_spend_wise" width="300" height="600">
   <img src="https://github.com/user-attachments/assets/39e6ebc8-89ca-46e3-9e63-45e3dd9f6e1b" alt="upload_spend_wise" width="300" height="600">

## Usage

1. **Login/Sign Up**: Users need to sign in or register with an email and password.

2. **Set Budget**: Users can set their budget.

3. **Add Transactions**: Users can add transactions to track their expenses.

4. **View Transactions**: Users can view their transaction history in the app's main screen.

5. **Logout**: Users can securely log out of the app.

## Challenges Faced
### 1. Firebase Integration
Integrating Firebase services, particularly the Realtime Database and Authentication, required careful management of asynchronous data fetching and synchronization. Handling edge cases, such as network failures and data consistency, was a significant challenge.

### 2. UI/UX Design
Designing a user-friendly interface that is both functional and aesthetically pleasing was challenging, especially when dealing with complex features like budget management and transaction tracking. Ensuring a seamless user experience across different devices and screen sizes also required thorough testing and adjustments.

### 3. RecyclerView and Data Handling
Managing large amounts of transaction data in RecyclerView, including ensuring smooth scrolling and efficient data binding, presented challenges. Implementing features like data persistence and restoring the state of the RecyclerView on app restarts added complexity.

### 4. State Management
Properly managing the state of various UI components, particularly when the app is sent to the background or when navigating between fragments, was challenging. Ensuring data consistency and avoiding memory leaks required a solid understanding of the Android lifecycle.

### 5. Error Handling and Debugging
Dealing with unexpected errors, especially related to network operations and Firebase interactions, required robust error handling mechanisms. Debugging issues, particularly those that were intermittent or only occurred in specific scenarios, was time-consuming.

### 6. Time Management
Balancing the implementation of features with testing and debugging within the project timeline was challenging. Prioritizing tasks and making trade-offs between features and stability was essential to meet deadlines.

## Contact
For any inquiries, please reach out to:
- **Email**: nadavdnl@gmail.com

## Credits
Developed by [Nadav Daniel]
Course: Mobile Application Development
