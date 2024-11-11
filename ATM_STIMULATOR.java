import java.sql.*;
import java.util.*;

class Account {
    String user_Name;
    String password;
    String Accno;
    float balance = 0.0f;

    // MySQL database connection variables
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    public Account(String u, String p, String a) {
        user_Name = u;
        password = p;
        Accno = a;
        System.out.println("Account Created Successfully\n");
        connectDB();
        createAccountInDB();
    }

    // Method to connect to MySQL
    public void connectDB() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish a connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankDB", "root", "vam2004");
            System.out.println("Connected to the database.\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Store account in DB
    public void createAccountInDB() {
        try {
            String query = "INSERT INTO accounts(accno, username, password, balance) VALUES (?, ?, ?, ?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, Accno);
            pstmt.setString(2, user_Name);
            pstmt.setString(3, password);
            pstmt.setFloat(4, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sign in method with DB validation
    public boolean Signin() {
        boolean login = false;
        Scanner cs = new Scanner(System.in);
        System.out.println("Enter user name - ");
        String Username = cs.nextLine();

        try {
            String query = "SELECT * FROM accounts WHERE username = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, Username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.print("Enter your password - ");
                String Password = cs.nextLine();
                if (Password.equals(rs.getString("password"))) {
                    System.out.println("Login Successfully\n");
                    login = true;
                    this.balance = rs.getFloat("balance"); // load the balance
                } else {
                    System.out.println("Incorrect Password!!");
                }
            } else {
                System.out.println("Username not Found!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return login;
    }

    // Deposit money and update DB
    public void deposit() {
        Scanner cs = new Scanner(System.in);
        System.out.println("Enter amount to deposit:\n");
        float amount = cs.nextFloat();
        if (amount <= 0.0) {
            System.out.print("Enter valid amount\n");
        } else {
            balance = balance + amount;
            updateBalance();
            logTransaction("Deposit", amount);
            System.out.println(amount + " deposited successfully\n");
        }
    }

    // Withdraw money and update DB
    public void withdrawl() {
        Scanner cs = new Scanner(System.in);
        System.out.println("Enter amount to Withdraw\n");
        float withdrawl_amount = cs.nextFloat();
        if (withdrawl_amount > balance) {
            System.out.println("Account Balance is Low\n");
        } else {
            balance = balance - withdrawl_amount;
            updateBalance();
            logTransaction("Withdraw", withdrawl_amount);
            System.out.println(withdrawl_amount + " withdrawn successfully\n");
        }
    }

    // Transfer funds and update DB
    public void fund_Transfer() {
        Scanner cs = new Scanner(System.in);
        System.out.print("Enter Recipient Name: ");
        String recepient_Name = cs.nextLine();
        System.out.println("Enter amount to transfer\n");
        float amt_transfer = cs.nextFloat();

        if (amt_transfer > 0.0 && balance >= amt_transfer) {
            balance = balance - amt_transfer;
            updateBalance();
            logTransaction("Fund Transfer", amt_transfer);
            System.out.println(amt_transfer + " transferred successfully to " + recepient_Name + "\n");
        } else {
            System.out.println("Invalid Account Balance\n");
        }
    }

    // Method to check balance
    public void Check_balance() {
        System.out.println("Your Account Balance is " + balance + " rupees only.\n");
    }

    // Update balance in the database
    public void updateBalance() {
        try {
            String query = "UPDATE accounts SET balance = ? WHERE accno = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setFloat(1, balance);
            pstmt.setString(2, Accno);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Log transaction in DB
    public void logTransaction(String transType, float amount) {
        try {
            String query = "INSERT INTO transactions(accno, trans_type, amount) VALUES (?, ?, ?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, Accno);
            pstmt.setString(2, transType);
            pstmt.setFloat(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class ATM_STIMULATOR {
    public static void main(String args[]){
        Scanner cs=new Scanner(System.in);
        System.out.println("Welcome to XXXX Bank\n");
        System.out.print("Enter your User name : ");
        String username=cs.next();
        System.out.print("Enter your Password : ");
        String password=cs.next();
        System.out.print("Enter your Account Number : ");
        String acno=cs.next();
        Account a=new Account(username,password,acno);
        System.out.println("Do you want to Login: yes/no \n");
        String res=cs.next();
        if(res.equalsIgnoreCase("Yes")){
            if(a.Signin()){
                boolean finish=false;
                while(!finish){
                    System.out.println("\n1.Balance Enquiry \n2.Cash Withdrawl \n3.Cash Deposit \n4.Fund Transfer \n5.Update Balance \n6.Transaction logs \n7.Quit\n");
                    System.out.println("Please select any banking option\n");
                    int option=cs.nextInt();
                    switch(option){
                        case 1:
                        a.Check_balance();
                        break;
                        case 2:
                        a.withdrawl();
                        break;
                        case 3:
                        a.deposit();
                        break;
                        case 4:
                        a.fund_Transfer();
                        break;
                        case 5:
                        a.updateBalance();
                        break;
                        case 6:
                        System.out.println("Choose any Transaction : \n1.Deposit \n2.Withdraw \n3.Fund Transfer");
                        int logOption=cs.nextInt(); 
                        String logTn="";
                        if(logOption==1)
                          logTn="Deposit"; 
                        if(logOption==2)
                          logTn="Withdraw";
                        if(logOption==3)
                          logTn="Fund Transfer";
                        System.out.println("Enter amount");  
                        float amt=cs.nextFloat();      
                        a.logTransaction(logTn,amt);
                        case 7:
                        finish=true;
                        System.out.println("Thank you,Have a nice day\n");
                        break;
                        default:
                        System.out.println("Choose any Valid Option \n");
                        break;
                    }
                }
            }
           
        }
        else{
            System.out.println("Thank you,Have a nice day\n");
        }
        
    }
}