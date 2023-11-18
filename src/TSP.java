import java.io.*;
import java.util.ArrayList;

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
    static int[][] distanceMatrix = {
            // the distance matrix with the 10 stops
            {0, 8, 6, 9, 10, 11, 14, 15, 12, 7},
            {8, 0, 5, 12, 13, 15, 9, 10, 11, 6},
            {6, 5, 0, 7, 8, 9, 12, 14, 10, 7},
            {9, 12, 7, 0, 6, 8, 14, 16, 11, 13},
            {10, 13, 8, 6, 0, 5, 12, 11, 10, 15},
            {11, 15, 9, 8, 5, 0, 7, 6, 9, 14},
            {14, 9, 12, 14, 12, 7, 0, 5, 10, 8},
            {15, 10, 14, 16, 11, 6, 5, 0, 7, 9},
            {12, 11, 10, 11, 10, 9, 10, 7, 0, 5},
            {7, 6, 7, 13, 15, 14, 8, 9, 5, 0},

    };

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
    public static int[] generateShortestRoute() {
        int deliveryStops = distanceMatrix.length;//this is the number of delivery stops
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


        //check if there are negative values in the distance matrix
        //check if the number of deliveries is more than 10
        //If so display an error message
        checkNegativeDistances();
        checkBounds();



       //define the coordinates of the delivery stops
        int[][] coordinates = {
                {distanceMatrix[0][0], distanceMatrix[0][1]},
                {distanceMatrix[1][0], distanceMatrix[1][1]},
                {distanceMatrix[2][0], distanceMatrix[2][1]},
                {distanceMatrix[3][0], distanceMatrix[3][1]},
                {distanceMatrix[4][0], distanceMatrix[4][1]},
                {distanceMatrix[5][0], distanceMatrix[5][1]},
                {distanceMatrix[6][0], distanceMatrix[6][1]},
                {distanceMatrix[7][0], distanceMatrix[7][1]},
                {distanceMatrix[8][0], distanceMatrix[8][1]},
                {distanceMatrix[9][0], distanceMatrix[9][1]},

        };

        // Create an ArrayList of Invoice objects
        ArrayList<Invoice> invoices = new ArrayList<Invoice>();
        // Add 10 invoices to the ArrayList
        invoices.add(new Invoice("John", 100.00, "123 Main St", 101));
        invoices.add(new Invoice("Jane", 200.00, "456 Main St", 102));
        invoices.add(new Invoice("Joe", 300.00, "789 Main St", 103));
        invoices.add(new Invoice("Jill", 400.00, "101 Main St", 104));
        invoices.add(new Invoice("Jack", 500.00, "112 Main St", 105));
        invoices.add(new Invoice("Jenny", 600.00, "131 Main St", 106));
        invoices.add(new Invoice("Jim", 700.00, "415 Main St", 107));
        invoices.add(new Invoice("Jen", 800.00, "161 Main St", 108));
        invoices.add(new Invoice("Jesse", 900.00, "718 Main St", 109));
        invoices.add(new Invoice("Jade", 1000.00, "191 Main St", 110));



        // Display the route and invoice details BEFORE serialization
        System.out.println("Shortest Route (Before Serialization):");

        int[] shortestRouteBefore = generateShortestRoute();
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
