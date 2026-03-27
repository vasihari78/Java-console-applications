import java.io.*;
import java.util.*;

class Auth {

    static void signup(String email, String pass, String role) throws Exception {
        FileWriter fw = new FileWriter("users.txt", true);
        fw.write(email + "," + pass + "," + role + ",30000\n");
        fw.close();
        System.out.println("Signup success");
    }

    static String login(String email, String pass) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("users.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");

            if (data[0].equals(email) && data[1].equals(pass)) {
                return data[2]; // role
            }
        }
        return null;
    }
}
class VehicleOps {

    static void addVehicle(String id, String name, String type, int count, double price) throws Exception {
        FileWriter fw = new FileWriter("vehicles.txt", true);
        fw.write(id + "," + name + "," + type + "," + count + "," + price + ",true\n");
        fw.close();
    }

    static void viewVehicles() throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"));
    String line;

    System.out.println("\n----- VEHICLE LIST -----");

    while ((line = br.readLine()) != null) {
        String[] v = line.split(",");

        if (v[5].equals("true")) {
            System.out.println("ID           : " + v[0]);
            System.out.println("Name         : " + v[1]);
            System.out.println("Type         : " + v[2]);
            System.out.println("Available    : " + v[3]);
            System.out.println("Price/Day    : ₹" + v[4]);
            System.out.println("Serviced     : " + v[5]);
            System.out.println("---------------------------");
        }
    }
    br.close();
}

    static void searchVehicle(String name) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"));
    String line;

    while ((line = br.readLine()) != null) {
        String[] v = line.split(",");

        if (v[1].equalsIgnoreCase(name)) {
            System.out.println("\n--- VEHICLE FOUND ---");
            System.out.println("ID        : " + v[0]);
            System.out.println("Name      : " + v[1]);
            System.out.println("Type      : " + v[2]);
            System.out.println("Available : " + v[3]);
            System.out.println("Price     : ₹" + v[4]);
            System.out.println("----------------------");
        }
    }
    br.close();
}
}
class RentOps {

    static void rent(String email, String vehicleId, int days) throws Exception {

    BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"));
    List<String> list = new ArrayList<>();
    String line;
    double price = 0;

    while ((line = br.readLine()) != null) {
        String[] v = line.split(",");

        if (v[0].equals(vehicleId) && Integer.parseInt(v[3]) > 0) {
            int count = Integer.parseInt(v[3]) - 1;
            price = Double.parseDouble(v[4]);

            line = v[0] + "," + v[1] + "," + v[2] + "," + count + "," + v[4] + "," + v[5];
        }
        list.add(line);
    }
    br.close();

    FileWriter fw = new FileWriter("vehicles.txt");
    for (String l : list) fw.write(l + "\n");
    fw.close();

    double cost = price * days;


    fw = new FileWriter("rentals.txt", true);
    fw.write(email + "," + vehicleId + "," + days + "," + cost + "\n");
    fw.close();

   
    System.out.println("\n Vehicle Rented Successfully!");
    System.out.println("Vehicle ID   : " + vehicleId);
    System.out.println("Days         : " + days);
    System.out.println("Total Amount : ₹" + cost);
}
}
class Fine {

    static double calculate(double cost, int kms, String damage) {

        double fine = 0;

        if (kms > 500)
            fine += cost * 0.15;

        if (damage.equals("LOW"))
            fine += cost * 0.20;
        else if (damage.equals("MEDIUM"))
            fine += cost * 0.50;
        else if (damage.equals("HIGH"))
            fine += cost * 0.75;

        return fine;
    }
}
class Reports {

   static void allRentals() throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("rentals.txt"));
    String line;

    System.out.println("\n----- RENTAL HISTORY -----");

    while ((line = br.readLine()) != null) {

        if (line.trim().isEmpty()) continue;

        String[] r = line.split(",");
        if (r.length < 4) continue;

        System.out.println("User Email   : " + r[0]);
        System.out.println("Vehicle ID   : " + r[1]);
        System.out.println("Days         : " + r[2]);
        System.out.println("Amount Paid  : ₹" + r[3]);
        System.out.println("----------------------------");
    }

    br.close();
}

    static void unservicedVehicles() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String[] v = line.split(",");
            if (v[5].equals("false")) {
                System.out.println(v[1]);
            }
        }
    }
}


public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        while (true) {
            System.out.println("\n1. Signup\n2. Login\n3. Exit");
            int ch = sc.nextInt(); sc.nextLine();

            if (ch == 1) {
                System.out.print("Email: ");
                String email = sc.nextLine();

                System.out.print("Password: ");
                String pass = sc.nextLine();

                System.out.print("Role (ADMIN/USER): ");
                String role = sc.nextLine();

                Auth.signup(email, pass, role);
            }

            else if (ch == 2) {
                System.out.print("Email: ");
                String email = sc.nextLine();

                System.out.print("Password: ");
                String pass = sc.nextLine();

                String role = Auth.login(email, pass);

                if (role == null) {
                    System.out.println("Invalid Login!");
                } 
                else if (role.equals("ADMIN")) {
                    adminMenu();
                } 
                else {
                    userMenu(email);
                }
            }

            else break;
        }
    }

    // 🔹 ADMIN MENU
    static void adminMenu() throws Exception {
        while (true) {
            System.out.println("\nADMIN MENU");
            System.out.println("1.Add Vehicle\n2.View Vehicles\n3.Search\n4.Reports\n5.Logout");

            int ch = sc.nextInt(); sc.nextLine();

            switch (ch) {
                case 1:
                    System.out.print("ID: ");
                    String id = sc.nextLine();

                    System.out.print("Name: ");
                    String name = sc.nextLine();

                    System.out.print("Type(CAR/BIKE): ");
                    String type = sc.nextLine();

                    System.out.print("Count: ");
                    int count = sc.nextInt();

                    System.out.print("Price: ");
                    double price = sc.nextDouble();

                    VehicleOps.addVehicle(id, name, type, count, price);
                    break;

                case 2:
                    VehicleOps.viewVehicles();
                    break;

                case 3:
                    System.out.print("Enter Name: ");
                    String search = sc.nextLine();
                    VehicleOps.searchVehicle(search);
                    break;

                case 4:
                    Reports.allRentals();
                    break;

                case 5:
                    return;
            }
        }
    }

    // 🔹 USER MENU
    static void userMenu(String email) throws Exception {
        while (true) {
            System.out.println("\nUSER MENU");
            System.out.println("1.View Vehicles\n2.Rent\n3.My Rentals\n4.Logout");

            int ch = sc.nextInt(); sc.nextLine();

            switch (ch) {
                case 1:
                    VehicleOps.viewVehicles();
                    break;

                case 2:
                    System.out.print("Vehicle ID: ");
                    String vid = sc.nextLine();

                    System.out.print("Days: ");
                    int days = sc.nextInt();

                    RentOps.rent(email, vid, days);
                    break;

                case 3:
                    Reports.allRentals();
                    break;

                case 4:
                    return;
            }
        }
    }
}