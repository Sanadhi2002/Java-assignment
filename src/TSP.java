import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

// Define a Serializable Invoice class to store customer information
class Invoice implements Serializable {
    String name;
    double total;
    String address;
    int invoiceId;

    // Constructor for creating an Invoice object
    public Invoice(String name, double total, String address, int invoiceId) {
        this.name = name;
        this.total = total;
        this.address = address;
        this.invoiceId = invoiceId;
    }
}

public class TSP implements Serializable {
    // Define a distance matrix representing distances between delivery stops
    static int[][] distanceMatrix ;
    static int[][] coordinates;

    // Initialize the distance matrix
    public static void inputDistanceMatrix(int stops) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the distance matrix (" + stops + "x" + stops + "):");

        distanceMatrix = new int[stops][stops];
        for (int i = 0; i < stops; i++) {
            for (int j = 0; j < stops; j++) {
                System.out.println("Enter distance from stop " + i + " to stop " + j + ":");
                distanceMatrix[i][j] = scanner.nextInt();
            }
        }
    }

    public static void inputCoordinates(int stops) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the coordinates for each delivery stop (10 stops):");

        coordinates = new int[stops][2];
        for (int i = 0; i < stops; i++) {
            System.out.println("Enter x-coordinate for stop " + i + ":");
            coordinates[i][0] = scanner.nextInt();
            System.out.println("Enter y-coordinate for stop " + i + ":");
            coordinates[i][1] = scanner.nextInt();
        }
    }



    public static void inputInvoices(ArrayList<Invoice> invoices, int stops) {
        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < stops; i++) {
            System.out.println("Enter details for Invoice " + (i + 1) + ":");
            System.out.print("Name: ");
            String name = scanner.next();

            double total = 0;
            boolean validTotal = false;

            while (!validTotal) {
                try {
                    System.out.print("Total: ");
                    total = scanner.nextDouble();
                    validTotal = true;
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number for Total.");
                    scanner.next(); // Consume the invalid input
                }
            }

            System.out.print("Address: ");
            String address = scanner.next();

            int invoiceId = 0;
            boolean validId = false;

            while (!validId) {
                try {
                    System.out.print("Invoice ID: ");
                    invoiceId = scanner.nextInt();
                    validId = true;
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer for Invoice ID.");
                    scanner.next(); // Consume the invalid input
                }
            }

            invoices.add(new Invoice(name, total, address, invoiceId));
        }
    }





    //check if there are negative distances in the distance matrix
    public static boolean checkNegativeDistances() {

        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix.length; j++) {
                if (distanceMatrix[i][j] < 0) {

                    throw new IllegalArgumentException("Error: Negative distances are not allowed");


                }
            }
        }
        return false;
    }

    public static boolean checkBounds(){
        try{

            for (int i = 0; i < distanceMatrix.length; i++) {
                for (int j = 0; j < distanceMatrix.length; j++) {
                    if (distanceMatrix[i][j] >20) {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Error: Only 10 deliveries per run " + e);
            return true;
        }
    }

    static boolean[] delivered;

    // Function to generate the shortest delivery route
    public static int[] generateShortestRoute(int stops) {
        int deliveryStops = stops-1;//this is the number of delivery stops
        int[] shortestRoute = new int[deliveryStops];//initializing the shortest route array to store the shortest route
        delivered = new boolean[deliveryStops];//initializing an array to keep track of the stops that have been delivered to

        shortestRoute[0] = 0; // Start and end at the shop (stop 0)
        delivered[0] = true; //mark the shop as delivered to

        //start the recursive function to find the shortest route, starting index
        //is 1 because the shop is marked as the first stop
        generateShortestRouteRecursive(shortestRoute, 1);

        return shortestRoute;//return the shortest route
    }

    // Recursive function to find the shortest route
    public static void generateShortestRouteRecursive(int[] route, int step) {
        try {
            int deliveryStops = distanceMatrix.length;

            //check if all the stops have been visited
            if (step == deliveryStops) {
                return; // if all drop-offs visited, end at the shop
            }

            //getting the current delivery stop from the route
            int currentStop = route[step - 1];
            int shortestDistance = Integer.MAX_VALUE; //set the shortest distance to the maximum value
            int nextStop = -1;

            //finding the next stop with the shortest distance
            for (int i = 0; i < deliveryStops; i++) {
                //check if the stop has been delivered to and if the distance is shorter than the current shortest distance
                if (!delivered[i] && distanceMatrix[currentStop][i] < shortestDistance) {
                   //update the shortest distance and the next stop
                    shortestDistance = distanceMatrix[currentStop][i];
                    nextStop = i;
                }
            }

            //set the next stop in the route and mark it as delivered
            route[step] = nextStop;
            delivered[nextStop] = true;
            //call the function recursively to find the next stop
            generateShortestRouteRecursive(route, step + 1);
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Error: only 10 deliveries made per run");

        }
        catch (Exception e){
            System.out.println("Error: " + e);
        }
    }

    // Function to print the delivery route with invoice details
    public static void printRoute(int[] route, int[][] coordinates, ArrayList<Invoice> invoices) {
        System.out.println("Shortest Route:");
        double totalDistance = 0; // Initialize the total distance to 0
        //loop through each stop on the delivery route
        for (int i = 0; i < route.length - 1; i++) {
            //get the current delivery stop index
            int currentStop = route[i];

            //Get the index of the next delivery stop
            int nextStop = route[i + 1];

            //Get the distance between the current stop and the next stop
            int distance = distanceMatrix[currentStop][nextStop];

            //Add the distance to the total distance
            totalDistance += distance;

            // Display the invoice details along with the route, skip the  shop because it doesn't have invoice details (stop 0)
            if (currentStop != 0) {
                //retrieve the current invoice and the next invoice
                Invoice currentInvoice = invoices.get(currentStop);
                Invoice nextInvoice = invoices.get(nextStop);

                //display the current delivery stop details
                System.out.println("Stop " + currentStop + ": Coordinates (" + coordinates[currentStop][0] + ", " + coordinates[currentStop][1] + ") - Distance to next stop: " + distance);
                System.out.println("   Invoice ID: " + currentInvoice.invoiceId);
                System.out.println("   Current Customer: " + currentInvoice.name);
                System.out.println("   Total: " + currentInvoice.total);
                System.out.println("   Postal Address: " + currentInvoice.address);
                System.out.println("   Next Customer: " + nextInvoice.name);
            } else {
                //handle the case where the shop is the current stop(stop 0)
                System.out.println("Stop " + currentStop + ": Coordinates (" + coordinates[currentStop][0] + ", " + coordinates[currentStop][1] + ") - Distance to next stop: " + distance);
                System.out.println("    Shop");
            }
        }

        // Print the last stop (returning to the shop)
        int lastStop = route[route.length - 1];
        System.out.println("Stop " + lastStop + ": Coordinates (" + coordinates[lastStop][0] + ", " + coordinates[lastStop][1] + ")");
        System.out.println("   Returning to Shop");
        System.out.println("Total Distance: " + totalDistance);
    }

    public static void main(String[] args) throws IOException {


        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of delivery stops:");
        int stops = scanner.nextInt();

        inputDistanceMatrix(stops);
        inputCoordinates(stops);

        ArrayList<Invoice> invoices = new ArrayList<>();
        inputInvoices(invoices, stops);

        //check if there are negative values in the distance matrix
        //check if the number of deliveries is more than 10
        //If so display an error message
        checkNegativeDistances();






        // Display the route and invoice details BEFORE serialization
        System.out.println("Shortest Route (Before Serialization):");

        int[] shortestRouteBefore = generateShortestRoute(stops);
        printRoute(shortestRouteBefore, coordinates, invoices);

        // Serialize the shortest route and invoice objects
        FileOutputStream fos = new FileOutputStream("TSP.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        //serialize the shortest route
        oos.writeObject(shortestRouteBefore);

        // Serialize the ArrayList of invoices and add a marker to indicate the end
        for (Invoice invoice : invoices) {
            oos.writeObject(invoice);
        }
        oos.writeObject(false); // Marker to indicate the end

        // Deserialize and retrieve the shortest route and invoice objects
        FileInputStream fis = new FileInputStream("TSP.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);

        try {
            //deserialize the shortest route
            int[] route = (int[]) ois.readObject();

            // Use a generic type for deserialization
            ArrayList<Invoice> retrievedInvoices = new ArrayList<>();
            // Read objects in a loop until the marker (false) is encountered
            while (true) {
                Object obj = ois.readObject();
                if (obj instanceof Invoice) {
                    retrievedInvoices.add((Invoice) obj);
                } else if (obj instanceof Boolean && !(Boolean) obj) {
                    break; // End marker encountered, exit the loop
                }
            }
            // Display the route and invoice details AFTER serialization
            System.out.println("\nShortest Route (After Deserialization):");
            printRoute(route, coordinates, retrievedInvoices);
        } catch (ClassNotFoundException  e) {
            e.printStackTrace();
        }
    }
}
