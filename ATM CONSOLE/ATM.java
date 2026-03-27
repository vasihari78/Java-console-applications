import java.util.*;
import java.io.*;

public class ATM {

    static int machine_cash = 100000;
    static int total_transactions = 0;
    static Scanner sc = new Scanner(System.in);
    static Map<Integer, User> users = new HashMap<>();

    String password = "password";

    class Admin {

        void deposit() {
            System.out.println("Enter Amount to deposit in ATM:");
            int amount = sc.nextInt();

            System.out.println("CURRENT CASH : " + ATM.machine_cash);

            ATM.machine_cash += amount;

            System.out.println("DEPOSIT SUCCESSFUL");
            System.out.println("TOTAL CASH : " + ATM.machine_cash);
        }

        void total() {
            System.out.println("TOTAL TRANSACTIONS : " + ATM.total_transactions);
        }
    }

    static class User {

        int balance;
        int pin;
        int acc_number;

        Queue<Map<String,String>> miniStatement = new LinkedList<>();

        User(int acc_number,int balance,int pin){
            this.balance = balance;
            this.pin = pin;
            this.acc_number = acc_number;
        }

        void addTransaction(String type,int amount){

            Map<String,String> txn = new HashMap<>();

            txn.put("Type",type);
            txn.put("Amount",String.valueOf(amount));
            txn.put("Balance",String.valueOf(this.balance));

            if(miniStatement.size()==6){
                miniStatement.poll();
            }

            miniStatement.add(txn);
        }

        void saveTransactionToFile(String type,int amount){

            try{
                FileWriter fw = new FileWriter("transactions.txt",true);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("Account:"+acc_number+
                        " | Type:"+type+
                        " | Amount:"+amount+
                        " | Balance:"+balance);

                bw.newLine();
                bw.close();
            }
            catch(Exception e){
                System.out.println("File Error");
            }
        }

        void withdraw(){

            System.out.println("Enter withdraw amount:");
            int n = sc.nextInt();

            if(n<=this.balance && n<=ATM.machine_cash && n%100==0){

                ATM.machine_cash -= n;
                this.balance -= n;
                ATM.total_transactions++;

                addTransaction("Withdraw",n);
                saveTransactionToFile("Withdraw",n);

                saveAccountsToFile();

                System.out.println("WITHDRAW SUCCESSFUL");
                System.out.println("AVAILABLE BALANCE : "+this.balance);
            }
            else{
                System.out.println("INVALID OR INSUFFICIENT AMOUNT");
            }
        }

        void deposit(){

            System.out.println("Enter deposit amount:");
            int n = sc.nextInt();

            this.balance += n;
            ATM.machine_cash += n;
            ATM.total_transactions++;

            addTransaction("Deposit",n);
            saveTransactionToFile("Deposit",n);

            saveAccountsToFile();

            System.out.println("DEPOSIT SUCCESSFUL");
            System.out.println("AVAILABLE BALANCE : "+this.balance);
        }

        void balance_check(){
            System.out.println("AVAILABLE BALANCE : "+this.balance);
        }

        void pin_change(){

            System.out.println("ENTER OLD PIN:");
            int op = sc.nextInt();

            if(op==this.pin){

                System.out.println("ENTER NEW PIN:");
                int np = sc.nextInt();

                this.pin = np;

                saveAccountsToFile();

                System.out.println("PIN CHANGED SUCCESSFULLY");
            }
            else{
                System.out.println("INVALID PIN");
            }
        }

        void mini_statement(){

            if(miniStatement.isEmpty()){
                System.out.println("NO TRANSACTIONS AVAILABLE");
                return;
            }

            System.out.println("\n---- MINI STATEMENT (Last 6 Transactions) ----");

            for(Map<String,String> txn : miniStatement){

                System.out.println(
                        "Type: "+txn.get("Type")+
                                " | Amount: "+txn.get("Amount")+
                                " | Balance: "+txn.get("Balance")
                );
            }
        }

        void readTransactionsFromFile(){

            try{
                BufferedReader br = new BufferedReader(new FileReader("transactions.txt"));
                String line;

                System.out.println("\n--- TRANSACTION HISTORY ---");

                while((line = br.readLine()) != null){

                    if(line.contains("Account:"+acc_number)){
                        System.out.println(line);
                    }
                }

                br.close();
            }
            catch(Exception e){
                System.out.println("No transaction file found");
            }
        }
    }

    static void loadAccounts(){

        try{
            BufferedReader br = new BufferedReader(new FileReader("accounts.txt"));
            String line;

            while((line = br.readLine()) != null){

                String[] data = line.split(",");

                int acc = Integer.parseInt(data[0]);
                int bal = Integer.parseInt(data[1]);
                int pin = Integer.parseInt(data[2]);

                users.put(acc,new User(acc,bal,pin));
            }

            br.close();
        }
        catch(Exception e){
            System.out.println("Account file not found");
        }
    }

    static void saveAccountsToFile(){

        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("accounts.txt"));

            for(User u : users.values()){

                bw.write(u.acc_number+","+u.balance+","+u.pin);
                bw.newLine();
            }

            bw.close();
        }
        catch(Exception e){
            System.out.println("Error saving accounts");
        }
    }

    public static void main(String args[]){

        loadAccounts();

        ATM atm = new ATM();
        Admin a = atm.new Admin();

        System.out.println("ENTER ACCESS TYPE A:ADMIN , U:USER");
        char x = sc.next().charAt(0);

        if(x=='A'||x=='a'){

            System.out.println("ENTER ADMIN PASSWORD:");
            String pass = sc.next();

            if(pass.equals(atm.password)){

                System.out.println("D : Deposit ATM Cash");
                System.out.println("T : Total Transactions");

                char y = sc.next().charAt(0);

                if(y=='D'||y=='d'){
                    a.deposit();
                }
                else if(y=='T'||y=='t'){
                    a.total();
                }
            }
        }

        else if(x=='U'||x=='u'){

            System.out.println("Enter Account Number:");
            int acc_no = sc.nextInt();

            User user = users.get(acc_no);

            if(user == null){
                System.out.println("INVALID ACCOUNT");
                return;
            }

            System.out.println("Enter PIN:");
            int pass = sc.nextInt();

            if(user.pin==pass){

                while(true){

                    System.out.println("\n1 Withdraw");
                    System.out.println("2 Balance Check");
                    System.out.println("3 Change PIN");
                    System.out.println("4 Deposit");
                    System.out.println("5 Mini Statement");
                    System.out.println("6 File Transaction History");
                    System.out.println("7 Exit");

                    int choice = sc.nextInt();

                    switch(choice){

                        case 1: user.withdraw(); break;
                        case 2: user.balance_check(); break;
                        case 3: user.pin_change(); break;
                        case 4: user.deposit(); break;
                        case 5: user.mini_statement(); break;
                        case 6: user.readTransactionsFromFile(); break;
                        case 7: return;

                        default: System.out.println("INVALID OPTION");
                    }
                }
            }
            else{
                System.out.println("INCORRECT PIN");
            }
        }
    }
}