package backend.api.repository;

import backend.api.dto.OperationDTO;
import backend.api.entity.Location;
import backend.api.entity.Connection;

import com.github.javafaker.Faker;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class OperationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final Faker faker;
    public OperationRepository(JdbcTemplate jdbcTemplate ) {
        this.jdbcTemplate = jdbcTemplate;
        this.faker = new Faker(Locale.ENGLISH);
    }

    private List<Location> loadLocations(String cityName) {
        String sql;

        if (cityName != null) {
            sql = "SELECT l.id, l.id_city, l.name, l.poz_x, l.poz_y, l.st, l.fi " +
                    "FROM locations l " +
                    "JOIN cities c ON l.id_city = c.id " +
                    "WHERE c.name = ?";

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Location location = new Location();
                location.setId(rs.getLong("id"));
                location.setIdCity(rs.getLong("id_city"));
                location.setName(rs.getString("name"));
                location.setPozX(rs.getDouble("poz_x"));
                location.setPozY(rs.getDouble("poz_y"));
                location.setStart(rs.getBoolean("st"));
                location.setEnd(rs.getBoolean("fi"));
                return location;
            }, cityName);
        } else {
            sql = "SELECT id, id_city, name, poz_x, poz_y, st, fi FROM locations";

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Location location = new Location();
                location.setId(rs.getLong("id"));
                location.setIdCity(rs.getLong("id_city"));
                location.setName(rs.getString("name"));
                location.setPozX(rs.getDouble("poz_x"));
                location.setPozY(rs.getDouble("poz_y"));
                location.setStart(rs.getBoolean("st"));
                location.setEnd(rs.getBoolean("fi"));
                return location;
            });
        }
    }

    private List<Connection> loadConnections() {
        String sql = "SELECT id, id_int, id_ext FROM connections";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Connection connection = new Connection();
            connection.setId(rs.getLong("id"));
            connection.setIdInt(rs.getLong("id_int"));
            connection.setIdExt(rs.getLong("id_ext"));
            return connection;
        });
    }

    private Map<Long, List<Long>> buildGraph(List<Location> locations, List<Connection> connections) {
        Map<Long, List<Long>> graph = new HashMap<>();

        for (Location location : locations) {
            graph.put(location.getId(), new ArrayList<>());
        }

        for (Connection connection : connections) {
            if (graph.containsKey(connection.getIdInt())) {
                graph.get(connection.getIdInt()).add(connection.getIdExt());
            }
        }

        return graph;
    }

    public OperationDTO findShortestPath(String cityName) throws Exception {
        try {
            List<Location> locations = loadLocations(cityName);
            List<Connection> connections = loadConnections();

            if (locations.isEmpty()) {
                throw new Exception("No locations found in city: " + cityName);
            }

            Location startLocation = locations.stream()
                    .filter(Location::getStart)
                    .findFirst()
                    .orElseThrow(() -> new Exception("No start location found (st=true)"));

            Location endLocation = locations.stream()
                    .filter(Location::getEnd)
                    .findFirst()
                    .orElseThrow(() -> new Exception("No end location found (fi=true)"));

            Map<Long, List<Long>> graph = buildGraph(locations, connections);

            Map<Long, Long> parentMap = new HashMap<>();
            Queue<Long> queue = new LinkedList<>();
            Set<Long> visited = new HashSet<>();

            queue.add(startLocation.getId());
            visited.add(startLocation.getId());

            boolean found = false;

            while (!queue.isEmpty() && !found) {
                Long currentId = queue.poll();

                if (currentId.equals(endLocation.getId())) {
                    found = true;
                    break;
                }

                for (Long neighborId : graph.getOrDefault(currentId, Collections.emptyList())) {
                    if (!visited.contains(neighborId)) {
                        queue.add(neighborId);
                        visited.add(neighborId);
                        parentMap.put(neighborId, currentId);
                    }
                }
            }

            if (!found) {
                throw new Exception("No path exists between start and end locations");
            }

            List<Long> path = new ArrayList<>();
            Long currentId = endLocation.getId();

            while (currentId != null) {
                path.add(0, currentId);
                currentId = parentMap.get(currentId);
            }
            List<String> locationNames = buildLocationNamesList(path, locations);
            OperationDTO result = new OperationDTO();
            result.setNames(locationNames);
            return result;
        } catch (Exception e) {
            throw new Exception("Nu s-a gasit un drum intre locatiile date: " + e.getMessage());
        }
    }

    public OperationDTO findLongestPath(String cityName) throws Exception {
        try {
            List<Location> locations = loadLocations(cityName);
            List<Connection> connections = loadConnections();

            if (locations.isEmpty()) {
                throw new Exception("No locations found in city: " + cityName);
            }

            Location startLocation = locations.stream()
                    .filter(Location::getStart)
                    .findFirst()
                    .orElseThrow(() -> new Exception("No start location found (st=true)"));

            Location endLocation = locations.stream()
                    .filter(Location::getEnd)
                    .findFirst()
                    .orElseThrow(() -> new Exception("No end location found (fi=true)"));

            Map<Long, List<Long>> graph = buildGraph(locations, connections);

            List<List<Long>> allPaths = new ArrayList<>();
            Stack<List<Long>> stack = new Stack<>();

            List<Long> initialPath = new ArrayList<>();
            initialPath.add(startLocation.getId());
            stack.push(initialPath);

            while (!stack.isEmpty()) {
                List<Long> currentPath = stack.pop();
                Long currentNode = currentPath.get(currentPath.size() - 1);

                if (currentNode.equals(endLocation.getId())) {
                    allPaths.add(new ArrayList<>(currentPath));
                    continue;
                }

                for (Long neighbor : graph.getOrDefault(currentNode, Collections.emptyList())) {
                    if (!currentPath.contains(neighbor)) {
                        List<Long> newPath = new ArrayList<>(currentPath);
                        newPath.add(neighbor);
                        stack.push(newPath);
                    }
                }
            }

            if (allPaths.isEmpty()) {
                throw new Exception("No path exists between start and end locations");
            }

            List<Long> longestPath = allPaths.stream()
                    .max(Comparator.comparingInt(List::size))
                    .orElseThrow(() -> new Exception("No path found"));

            List<String> locationNames = buildLocationNamesList(longestPath, locations);

            OperationDTO result = new OperationDTO();
            result.setNames(locationNames);
            return result;
        } catch (Exception e) {
            throw new Exception("Nu s-a gasit un drum intre locatiile date: " + e.getMessage());
        }
    }

    public OperationDTO findMinimumCycle(String cityName) throws Exception {
        try {
            List<Location> locations = loadLocations(cityName);
            List<Connection> connections = loadConnections();

            if (locations.isEmpty()) {
                throw new Exception("No locations found in city: " + cityName);
            }

            Location startLocation = locations.stream()
                    .filter(Location::getStart)
                    .findFirst()
                    .orElseThrow(() -> new Exception("No start location found (st=true)"));

            Map<Long, List<Long>> graph = buildGraph(locations, connections);

            List<Long> shortestCycle = null;
            int shortestLength = Integer.MAX_VALUE;

            Map<Long, Long> parentMap = new HashMap<>();
            Map<Long, Integer> distance = new HashMap<>();
            Queue<Long> queue = new LinkedList<>();
            Set<Long> visited = new HashSet<>();

            queue.add(startLocation.getId());
            visited.add(startLocation.getId());
            distance.put(startLocation.getId(), 0);

            while (!queue.isEmpty()) {
                Long currentId = queue.poll();

                for (Long neighborId : graph.getOrDefault(currentId, Collections.emptyList())) {
                    if (visited.contains(neighborId) && !neighborId.equals(parentMap.get(currentId))) {
                        int cycleLength = distance.get(currentId) + distance.get(neighborId) + 1;
                        if (shortestCycle == null || cycleLength < shortestLength) {
                            List<Long> cycle = new ArrayList<>();
                            Long backtrackNode = currentId;
                            while (backtrackNode != null) {
                                cycle.add(backtrackNode);
                                backtrackNode = parentMap.get(backtrackNode);
                            }

                            Collections.reverse(cycle);

                            backtrackNode = neighborId;
                            List<Long> part2 = new ArrayList<>();
                            while (backtrackNode != null && !cycle.contains(backtrackNode)) {
                                part2.add(backtrackNode);
                                backtrackNode = parentMap.get(backtrackNode);
                            }

                            int commonIndex = cycle.indexOf(backtrackNode);
                            List<Long> finalCycle = new ArrayList<>(cycle.subList(commonIndex, cycle.size()));
                            finalCycle.addAll(part2);

                            if (!finalCycle.get(0).equals(finalCycle.get(finalCycle.size() - 1))) {
                                finalCycle.add(finalCycle.get(0));
                            }

                            shortestCycle = finalCycle;
                            shortestLength = cycleLength;
                        }
                    }
                    else if (!visited.contains(neighborId)) {
                        queue.add(neighborId);
                        visited.add(neighborId);
                        parentMap.put(neighborId, currentId);
                        distance.put(neighborId, distance.get(currentId) + 1);
                    }
                }
            }

            if (shortestCycle == null) {
                throw new Exception("No cycle found starting from the start location");
            }

            int startIndex = shortestCycle.indexOf(startLocation.getId());
            if (startIndex > 0) {
                List<Long> reorderedCycle = new ArrayList<>();

                for (int i = startIndex; i < shortestCycle.size() - 1; i++) {
                    reorderedCycle.add(shortestCycle.get(i));
                }

                for (int i = 0; i < startIndex; i++) {
                    reorderedCycle.add(shortestCycle.get(i));
                }

                reorderedCycle.add(startLocation.getId());

                shortestCycle = reorderedCycle;
            }

            List<String> locationNames = buildLocationNamesList(shortestCycle, locations);

            OperationDTO result = new OperationDTO();
            result.setNames(locationNames);
            return result;
        } catch (Exception e) {
            throw new Exception("Nu s-a gasit un circuit pentru locatia data: " + e.getMessage());
        }
    }

    private List<String> buildLocationNamesList(List<Long> locationIds, List<Location> locations)
    {
        Map<Long, String> locationNameMap = locations.stream()
                .collect(Collectors.toMap(Location::getId, Location::getName));

        return locationIds.stream()
                .map(locationNameMap::get)
                .collect(Collectors.toList());
    }

    public OperationDTO shortestPath(String cityName) throws Exception {
        return findShortestPath(cityName);
    }

    public OperationDTO cyclePath(String cityName) throws Exception {
        return findMinimumCycle(cityName);
    }

    public OperationDTO longestPath(String cityName) throws Exception {
        return findLongestPath(cityName);
    }

    public void createLocations() throws Exception {
        try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM connections WHERE id_int IN (SELECT id FROM locations WHERE id_city = 2) OR " +
                            "id_ext IN (SELECT id FROM locations WHERE id_city = 2)")) {
                pstmt.executeUpdate();
                System.out.println("Conexiuni existente sterse pentru orasul cu ID=2");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM informations WHERE id_location IN (SELECT id FROM locations WHERE id_city = 2)")) {
                pstmt.executeUpdate();
                System.out.println("Informatii existente sterse pentru orasul cu ID=2");
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM locations WHERE id_city = 2");
                System.out.println("Locatii existente sterse pentru orasul cu ID=2");
            }

            long maxId = 0;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(id), 0) as max_id FROM locations")) {
                if (rs.next()) {
                    maxId = rs.getLong("max_id");
                }
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER SEQUENCE locations_id_seq RESTART WITH " + (maxId + 1));
            }

            Random rand = new Random();
            int numLocations = rand.nextInt(16) + 5;

            boolean hasStart = false;
            boolean hasEnd = false;

            String insertSQL = "INSERT INTO locations (name, id_city, poz_x, poz_y, st, fi) VALUES (?, 2, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                for (int i = 0; i < numLocations; i++) {
                    boolean isStart = (i == numLocations - 2);
                    boolean isEnd = (i == numLocations - 1);

                    if (i == numLocations - 1 && !hasStart) {
                        isStart = true;
                    }

                    String name = generateLocationName();
                    double pozY = 27.54 + rand.nextDouble() * (27.68 - 27.54);
                    double pozX = 47.11 + rand.nextDouble() * (47.19 - 47.11);

                    pstmt.setString(1, name);
                    pstmt.setDouble(2, pozX);
                    pstmt.setDouble(3, pozY);
                    pstmt.setBoolean(4, isStart);
                    pstmt.setBoolean(5, isEnd);

                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    if (isStart) hasStart = true;
                    if (isEnd) hasEnd = true;
                }
            }

            System.out.println("S-au generat " + numLocations + " locatii noi pentru orasul cu ID=2");

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private String generateLocationName() {
        switch (new Random().nextInt(5)) {
            case 0:
                return faker.company().name() + " " + faker.address().cityName();
            case 1:
                return faker.address().streetName() + " " + faker.address().buildingNumber();
            case 2:
                return faker.commerce().department() + " " + faker.company().suffix();
            case 3:
                return faker.name().lastName() + " " + faker.address().cityName();
            default:
                return faker.funnyName().name() + " Plaza";
        }
    }

    public void createConnections() throws Exception {
        try (java.sql.Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            List<Integer> locationIds = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id FROM locations WHERE id_city = 2")) {
                while (rs.next()) {
                    locationIds.add(rs.getInt("id"));
                }
            }

            if (locationIds.size() < 2) {
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM connections WHERE id_int IN (SELECT id FROM locations WHERE id_city = 2) OR " +
                            "id_ext IN (SELECT id FROM locations WHERE id_city = 2)")) {
                pstmt.executeUpdate();
            }

            Random rand = new Random();
            String insertSQL = "INSERT INTO connections (id_int, id_ext) VALUES (?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                for (Integer sourceId : locationIds) {
                    int numConnections = rand.nextInt(3) + 1;

                    for (int i = 0; i < numConnections; i++) {

                        int destIndex;
                        Integer destId;

                        do {
                            destIndex = rand.nextInt(locationIds.size());
                            destId = locationIds.get(destIndex);
                        } while (destId.equals(sourceId));

                        try {
                            pstmt.setInt(1, sourceId);
                            pstmt.setInt(2, destId);
                            pstmt.executeUpdate();
                            pstmt.clearParameters();
                        } catch (SQLException e) {
                            if (!e.getSQLState().equals("23505")) {
                                throw e;
                            }
                        }
                    }
                }
            }

            System.out.println("Conexiuni generate pentru orasul cu ID=2");

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void createLocations(Long oras) throws Exception {
        try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM connections WHERE id_int IN (SELECT id FROM locations WHERE id_city = ?) OR " +
                            "id_ext IN (SELECT id FROM locations WHERE id_city = ?)")) {
                pstmt.setLong(1, oras);
                pstmt.setLong(2, oras);
                pstmt.executeUpdate();
                System.out.println("Conexiuni existente sterse pentru orasul cu ID=" + oras);
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM informations WHERE id_location IN (SELECT id FROM locations WHERE id_city = ?)")) {
                pstmt.setLong(1, oras);
                pstmt.executeUpdate();
                System.out.println("Informatii existente sterse pentru orasul cu ID=" + oras);
            }

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM locations WHERE id_city = ?")) {
                stmt.setLong(1, oras);
                stmt.executeUpdate();
                System.out.println("Locatii existente sterse pentru orasul cu ID=" + oras);
            }

            long maxId = 0;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(id), 0) as max_id FROM locations")) {
                if (rs.next()) {
                    maxId = rs.getLong("max_id");
                }
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER SEQUENCE locations_id_seq RESTART WITH " + (maxId + 1));
            }

            Random rand = new Random();
            int numLocations = rand.nextInt(16) + 5;

            boolean hasStart = false;
            boolean hasEnd = false;

            // Aici este schimbarea principală - folosim parametrul oras în loc de 2 hardcodat
            String insertSQL = "INSERT INTO locations (name, id_city, poz_x, poz_y, st, fi) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                for (int i = 0; i < numLocations; i++) {
                    boolean isStart = (i == numLocations - 2);
                    boolean isEnd = (i == numLocations - 1);

                    if (i == numLocations - 1 && !hasStart) {
                        isStart = true;
                    }

                    String name = generateLocationName();
                    double pozY = 27.54 + rand.nextDouble() * (27.68 - 27.54);
                    double pozX = 47.11 + rand.nextDouble() * (47.19 - 47.11);

                    pstmt.setString(1, name);
                    pstmt.setLong(2, oras); // Parametrul oras în loc de valoarea hardcodată 2
                    pstmt.setDouble(3, pozX);
                    pstmt.setDouble(4, pozY);
                    pstmt.setBoolean(5, isStart);
                    pstmt.setBoolean(6, isEnd);

                    pstmt.executeUpdate();
                    pstmt.clearParameters();

                    if (isStart) hasStart = true;
                    if (isEnd) hasEnd = true;
                }
            }

            System.out.println("S-au generat " + numLocations + " locatii noi pentru orasul cu ID=" + oras);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void createConnections(Long oras) throws Exception {
        try (java.sql.Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            List<Integer> locationIds = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM locations WHERE id_city = ?")) {
                stmt.setLong(1, oras);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        locationIds.add(rs.getInt("id"));
                    }
                }
            }

            if (locationIds.size() < 2) {
                System.out.println("Nu sunt suficiente locații pentru orașul cu ID=" + oras + " pentru a crea conexiuni");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM connections WHERE id_int IN (SELECT id FROM locations WHERE id_city = ?) OR " +
                            "id_ext IN (SELECT id FROM locations WHERE id_city = ?)")) {
                pstmt.setLong(1, oras);
                pstmt.setLong(2, oras);
                pstmt.executeUpdate();
            }

            Random rand = new Random();
            String insertSQL = "INSERT INTO connections (id_int, id_ext) VALUES (?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                for (Integer sourceId : locationIds) {
                    int numConnections = rand.nextInt(3) + 1;

                    for (int i = 0; i < numConnections; i++) {

                        int destIndex;
                        Integer destId;

                        do {
                            destIndex = rand.nextInt(locationIds.size());
                            destId = locationIds.get(destIndex);
                        } while (destId.equals(sourceId));

                        try {
                            pstmt.setInt(1, sourceId);
                            pstmt.setInt(2, destId);
                            pstmt.executeUpdate();
                            pstmt.clearParameters();
                        } catch (SQLException e) {
                            if (!e.getSQLState().equals("23505")) {
                                throw e;
                            }
                        }
                    }
                }
            }

            System.out.println("Conexiuni generate pentru orasul cu ID=" + oras);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void createInformations(Long oras) throws Exception {
        try (java.sql.Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            List<Integer> locationIds = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM locations WHERE id_city = ?")) {
                pstmt.setLong(1, oras);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        locationIds.add(rs.getInt("id"));
                    }
                }
            }

            if (locationIds.isEmpty()) {
                System.out.println("Nu exista locatii pentru orasul cu ID=" + oras);
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM informations WHERE id_location IN " +
                            "(SELECT id FROM locations WHERE id_city = ?)")) {
                pstmt.setLong(1, oras);
                pstmt.executeUpdate();
            }

            String insertSQL = "INSERT INTO informations (id_location, id_city, description, type, danger_zone, city) " +
                    "VALUES (?, NULL, ?, ?, FALSE, FALSE)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                for (Integer locationId : locationIds) {
                    String description = generateDescription();
                    String type = generateType();

                    pstmt.setInt(1, locationId);
                    pstmt.setString(2, description);
                    pstmt.setString(3, type);

                    try {
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        if (e.getSQLState().equals("23505")) {
                            try (PreparedStatement updateStmt = conn.prepareStatement(
                                    "UPDATE informations SET description = ?, type = ? WHERE id_location = ?")) {
                                updateStmt.setString(1, description);
                                updateStmt.setString(2, type);
                                updateStmt.setInt(3, locationId);
                                updateStmt.executeUpdate();
                            }
                        } else {
                            throw e;
                        }
                    }
                    pstmt.clearParameters();
                }
            }

            System.out.println("Informatii generate cu succes pentru locatiile din orasul cu ID=" + oras);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private String generateDescription() {
        StringBuilder description = new StringBuilder();

        description.append(faker.lorem().paragraph(3));

        description.append(" ").append(faker.company().catchPhrase()).append(". ");

        if (new Random().nextBoolean()) {
            description.append("Este situat in apropiere de ")
                    .append(faker.address().streetName())
                    .append(". ");
        }

        if (new Random().nextBoolean()) {
            description.append("Program: ")
                    .append(faker.number().numberBetween(8, 12))
                    .append(":00 - ")
                    .append(faker.number().numberBetween(16, 21))
                    .append(":00")
                    .append(". ");
        }

        description.append(faker.company().bs()).append(" ").append(faker.company().catchPhrase());

        return description.toString();
    }

    private String generateType() {
        return switch (new Random().nextInt(7)) {
            case 0 -> faker.commerce().department();
            case 1 -> faker.company().industry();
            case 2 -> faker.job().field() + " " + faker.company().buzzword();
            case 3 -> faker.food().ingredient() + " " + faker.food().dish();
            case 4 -> faker.address().country() + " " + faker.commerce().productName();
            case 5 -> faker.book().genre() + " Center";
            default -> faker.app().name() + " " + faker.company().suffix();
        };
    }
}