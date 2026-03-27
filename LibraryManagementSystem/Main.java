import java.io.*;
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        while (true) {
            System.out.println("\n1.Signup 2.Login 3.Exit");
            int ch = sc.nextInt(); sc.nextLine();

            if (ch == 1) signup();
            else if (ch == 2) login();
            else break;
        }
    }

    // 🔹 SIGNUP
    static void signup() throws Exception {
        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        System.out.print("Role (ADMIN/STUDENT): ");
        String role = sc.nextLine();

        FileWriter fw = new FileWriter("users.txt", true);
        fw.write(email + "," + pass + "," + role + ",1500\n");
        fw.close();

        System.out.println("Signup Success!");
    }

    // 🔹 LOGIN
    static void login() throws Exception {
        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        BufferedReader br = new BufferedReader(new FileReader("users.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String[] u = line.split(",");

            if (u[0].equals(email) && u[1].equals(pass)) {
                if (u[2].equals("ADMIN")) adminMenu();
                else userMenu(email);
                return;
            }
        }
        System.out.println("Invalid Login!");
    }

    // 🔹 ADMIN MENU
    static void adminMenu() throws Exception {
        while (true) {
            System.out.println("\nADMIN MENU");
            System.out.println("1.Add Book 2.View Books 3.Search 4.Reports 5.Logout");

            int ch = sc.nextInt(); sc.nextLine();

            switch (ch) {
                case 1 -> addBook();
                case 2 -> viewBooks();
                case 3 -> searchBook();
                case 4 -> reports();
                case 5 -> { return; }
            }
        }
    }

    // 🔹 USER MENU
    static void userMenu(String email) throws Exception {
        while (true) {
            System.out.println("\nUSER MENU");
            System.out.println("1.View Books 2.Borrow 3.Return 4.My Books 5.Logout");

            int ch = sc.nextInt(); sc.nextLine();

            switch (ch) {
                case 1 -> viewBooks();
                case 2 -> borrowBook(email);
                case 3 -> returnBook(email);
                case 4 -> viewMyBooks(email);
                case 5 -> { return; }
            }
        }
    }

    // 🔹 ADD BOOK
    static void addBook() throws Exception {
        System.out.print("ISBN: ");
        String isbn = sc.nextLine();

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Quantity: ");
        int q = sc.nextInt();

        System.out.print("Price: ");
        double price = sc.nextDouble();

        FileWriter fw = new FileWriter("books.txt", true);
        fw.write(isbn + "," + name + "," + q + "," + price + "\n");
        fw.close();

        System.out.println("Book Added!");
    }

    // 🔹 VIEW BOOKS
    static void viewBooks() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("books.txt"));
        String line;

        System.out.println("\n----- BOOK LIST -----");

        while ((line = br.readLine()) != null) {
            String[] b = line.split(",");

            System.out.println("ISBN      : " + b[0]);
            System.out.println("Name      : " + b[1]);
            System.out.println("Available : " + b[2]);
            System.out.println("Price     : ₹" + b[3]);
            System.out.println("----------------------");
        }
    }

    // 🔹 SEARCH BOOK
    static void searchBook() throws Exception {
        System.out.print("Enter Name/ISBN: ");
        String key = sc.nextLine();

        BufferedReader br = new BufferedReader(new FileReader("books.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String[] b = line.split(",");

            if (b[0].equals(key) || b[1].equalsIgnoreCase(key)) {
                System.out.println("Found: " + line);
            }
        }
    }

    // 🔹 BORROW BOOK
    static void borrowBook(String email) throws Exception {

        System.out.print("Enter ISBN: ");
        String isbn = sc.nextLine();

        BufferedReader br = new BufferedReader(new FileReader("books.txt"));
        List<String> list = new ArrayList<>();
        String line;
        double price = 0;

        while ((line = br.readLine()) != null) {
            String[] b = line.split(",");

            if (b[0].equals(isbn) && Integer.parseInt(b[2]) > 0) {
                int q = Integer.parseInt(b[2]) - 1;
                price = Double.parseDouble(b[3]);

                line = b[0] + "," + b[1] + "," + q + "," + b[3];
            }
            list.add(line);
        }
        br.close();

        FileWriter fw = new FileWriter("books.txt");
        for (String l : list) fw.write(l + "\n");
        fw.close();

        // store borrow
        fw = new FileWriter("borrow.txt", true);
        fw.write(email + "," + isbn + "," + System.currentTimeMillis() + "," + price + "\n");
        fw.close();

        System.out.println("Book Borrowed!");
    }

    // 🔹 RETURN BOOK
    static void returnBook(String email) throws Exception {

        System.out.print("Enter ISBN: ");
        String isbn = sc.nextLine();

        BufferedReader br = new BufferedReader(new FileReader("borrow.txt"));
        List<String> list = new ArrayList<>();
        String line;

        while ((line = br.readLine()) != null) {
            String[] r = line.split(",");

            if (r[0].equals(email) && r[1].equals(isbn)) {

                long borrowTime = Long.parseLong(r[2]);
                long now = System.currentTimeMillis();

                long days = (now - borrowTime) / (1000 * 60 * 60 * 24);

                double price = Double.parseDouble(r[3]);
                double fine = 0;

                if (days > 15) {
                    fine = (days - 15) * 2;
                    fine = Math.min(fine, price * 0.8);
                }

                System.out.println("Days Used: " + days);
                System.out.println("Fine: ₹" + fine);

                // store fine
                FileWriter fw = new FileWriter("fines.txt", true);
                fw.write(email + "," + isbn + "," + fine + "\n");
                fw.close();

                continue; // remove record
            }
            list.add(line);
        }
        br.close();

        FileWriter fw = new FileWriter("borrow.txt");
        for (String l : list) fw.write(l + "\n");
        fw.close();

        System.out.println("Book Returned!");
    }

    // 🔹 VIEW MY BOOKS
    static void viewMyBooks(String email) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("borrow.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.startsWith(email)) {
                System.out.println(line);
            }
        }
    }

    // 🔹 REPORTS
    static void reports() throws Exception {

        System.out.println("\n1.Low Stock 2.Not Borrowed 3.Heavy Borrowed");

        int ch = sc.nextInt();

        if (ch == 1) {
            BufferedReader br = new BufferedReader(new FileReader("books.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] b = line.split(",");
                if (Integer.parseInt(b[2]) < 2) {
                    System.out.println("Low Stock: " + b[1]);
                }
            }
        }

        if (ch == 2) {
            System.out.println("Manual check (basic version)");
        }

        if (ch == 3) {
            System.out.println("Check borrow.txt for frequency");
        }
    }
}