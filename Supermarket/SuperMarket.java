import java.io.*;
import java.util.*;

class User {
    int id;
    String email;
    String password;
    String role;

    User(int id, String email, String password, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

class Product {
    int id;
    String name;
    double price;

    Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

class CartItem {
    Product product;
    int quantity;

    CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}

public class SuperMarket {

    static Scanner sc = new Scanner(System.in);
    static ArrayList<CartItem> cart = new ArrayList<>();

    // LOGIN
    static String login(String email, String password) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("users.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String fileEmail = data[1];
                String filePassword = data[2];
                String role = data[3];

                if (fileEmail.equals(email) && filePassword.equals(password)) {
                    br.close();
                    return role;
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error reading users file");
        }
        return null;
    }

    // LOAD PRODUCTS
    static ArrayList<Product> loadProducts() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("products.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                products.add(new Product(
                        Integer.parseInt(data[0]),
                        data[1],
                        Double.parseDouble(data[2])
                ));
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error reading products file");
        }
        return products;
    }

    // VIEW PRODUCTS
    static void viewProducts() {
        ArrayList<Product> products = loadProducts();
        System.out.println("ID\tName\tPrice");
        for (Product p : products) {
            System.out.println(p.id + "\t" + p.name + "\t" + p.price);
        }
    }

    // ADD PRODUCT (ADMIN)
    static void addProduct() {
        try {
            FileWriter fw = new FileWriter("products.txt", true);

            System.out.println("Enter Product ID:");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.println("Enter Product Name:");
            String name = sc.nextLine();

            System.out.println("Enter Price:");
            double price = sc.nextDouble();

            fw.write(id + "," + name + "," + price + "\n");
            fw.close();

            System.out.println("Product Added!");
        } catch (Exception e) {
            System.out.println("Error adding product");
        }
    }

    // DELETE PRODUCT
    static void deleteProduct() {
        System.out.println("Enter Product ID to delete:");
        int deleteId = sc.nextInt();

        try {
            File inputFile = new File("products.txt");
            File tempFile = new File("temp.txt");

            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            FileWriter fw = new FileWriter(tempFile);

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int id = Integer.parseInt(data[0]);

                if (id != deleteId) {
                    fw.write(line + "\n");
                }
            }

            br.close();
            fw.close();

            inputFile.delete();
            tempFile.renameTo(inputFile);

            System.out.println("Product Deleted!");
        } catch (Exception e) {
            System.out.println("Error deleting product");
        }
    }

    // BUY PRODUCT
    static void buyProduct() {
        ArrayList<Product> products = loadProducts();
        viewProducts();

        System.out.println("Enter Product ID to buy:");
        int id = sc.nextInt();

        System.out.println("Enter Quantity:");
        int qty = sc.nextInt();

        for (Product p : products) {
            if (p.id == id) {
                cart.add(new CartItem(p, qty));
                System.out.println("Added to Cart!");
                return;
            }
        }
        System.out.println("Product Not Found");
    }

    // VIEW CART
    static void viewCart() {
        double total = 0;
        System.out.println("Cart Items:");
        for (CartItem item : cart) {
            double cost = item.product.price * item.quantity;
            total += cost;
            System.out.println(item.product.name + " x " + item.quantity + " = " + cost);
        }
        System.out.println("Total = " + total);
    }

    // CHECKOUT
    static void checkout() {
        viewCart();
        System.out.println("Checkout Complete. Thank you!");
        cart.clear();
    }

    // ADMIN MENU
    static void adminMenu() {
        while (true) {
            System.out.println("\nADMIN MENU");
            System.out.println("1.Add Product");
            System.out.println("2.View Products");
            System.out.println("3.Delete Product");
            System.out.println("4.Logout");

            int choice = sc.nextInt();

            switch (choice) {
                case 1: addProduct(); break;
                case 2: viewProducts(); break;
                case 3: deleteProduct(); break;
                case 4: return;
            }
        }
    }

    // CUSTOMER MENU
    static void customerMenu() {
        while (true) {
            System.out.println("\nCUSTOMER MENU");
            System.out.println("1.View Products");
            System.out.println("2.Buy Product");
            System.out.println("3.View Cart");
            System.out.println("4.Checkout");
            System.out.println("5.Logout");

            int choice = sc.nextInt();

            switch (choice) {
                case 1: viewProducts(); break;
                case 2: buyProduct(); break;
                case 3: viewCart(); break;
                case 4: checkout(); break;
                case 5: return;
            }
        }
    }

    // MAIN
    public static void main(String[] args) {
        System.out.println("===== SUPER MARKET SYSTEM =====");

        System.out.println("Enter Email:");
        String email = sc.next();

        System.out.println("Enter Password:");
        String password = sc.next();

        String role = login(email, password);

        if (role == null) {
            System.out.println("Invalid Login");
        } else if (role.equals("ADMIN")) {
            adminMenu();
        } else {
            customerMenu();
        }
    }
}