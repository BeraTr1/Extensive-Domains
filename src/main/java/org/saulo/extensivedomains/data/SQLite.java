package org.saulo.extensivedomains.data;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.managers.CitizenManager;
import org.saulo.extensivedomains.objects.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLite implements Data {
    private final File databaseFile;
    private CitizenManager citizenManager;

    public SQLite(ExtensiveDomains plugin, String location, CitizenManager citizenManager) {
        this.databaseFile = new File(plugin.getDataFolder() + File.separator + location);
        this.citizenManager = citizenManager;
    }

    public Connection connect() throws SQLException {
        String url = "jdbc:sqlite:" + this.databaseFile.getAbsolutePath();
        Connection connection = DriverManager.getConnection(url);

        System.out.println("Established connection to SQLite");
        return connection;
    }

    @Override
    public void saveAll() {
        try {
            Connection connection = connect();
            createCitizensTable(connection);
            createDomainsTable(connection);

            System.out.println("Saving data...");
            saveCitizens(connection);
            saveDomains(connection);
            System.out.println("Successfully saved all data!");

            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void loadAll() {
        try {
            Connection connection = connect();

            preloadAll(connection);

            System.out.println("Loading data...");
            loadCitizens(connection);
            loadDomains(connection);
            System.out.println("Successfully loaded all data!");

            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void preloadAll(Connection connection) {
        System.out.println("Preloading data...");
        preloadCitizens(connection);
        preloadDomains(connection);
        // todo claims (must be after domains) ?
        System.out.println("Successfully preloaded all data!");
    }

    // todo rename to getDataFromTable
    private ResultSet queryTable(Connection connection, String tableName) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT * FROM ").append(tableName);
            Statement statement = connection.createStatement();
            return statement.executeQuery(stringBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void createCitizensTable(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS Citizens(citizen_id varchar(255) primary key, domain_id varchar(255))";
            statement.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertCitizen(Connection connection, String citizenUUID, String domainUUID) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO Citizens (citizen_id, domain_id) VALUES (?, ?)");
            preparedStatement.setString(1, citizenUUID);
            preparedStatement.setString(2, domainUUID);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveCitizens(Connection connection) {
        List<Citizen> citizens = citizenManager.getCitizenInstances();
        System.out.println("\tTrying to save (" + citizens.size() + ") citizens...");

        for (Citizen citizen : citizens) {
            String citizenUUIDString = citizen.getUUID().toString();
            String domainUUIDString = citizen.getDomain().getUUID().toString();

            this.insertCitizen(connection, citizenUUIDString, domainUUIDString);
            System.out.printf("\t\tSaved citizen with uuid (%s)\n", citizenUUIDString);
        }
    }

    private void preloadCitizens(Connection connection) {
        try {
            ResultSet resultSet = this.queryTable(connection, "Citizens");

            if (resultSet == null) {
                System.out.println("\tNo citizens to preload!");
                return;
            }

            System.out.println("\tTrying to preload citizens...");


            while (resultSet.next()) {
                String citizenUUIDString = resultSet.getString("citizen_id");
                UUID citizenUUID = UUID.fromString(citizenUUIDString);
                Citizen citizen = new Citizen(citizenUUID);
                citizenManager.registerCitizen(citizen);
                System.out.printf("\t\tPreloaded citizen with uuid (%s)\n", citizenUUIDString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCitizens(Connection connection) {
        try {
            ResultSet resultSet = this.queryTable(connection, "Citizens");

            if (resultSet == null) {
                System.out.println("\tNo citizens to load!");
                return;
            }

            System.out.printf("\tTrying to load citizens...\n", resultSet.getFetchSize());

            while (resultSet.next()) {
                String citizenUUIDString = resultSet.getString("citizen_id");
                UUID citizenUUID = UUID.fromString(citizenUUIDString);
                Citizen citizen = citizenManager.getRegisteredCitizen(citizenUUID);
                System.out.printf("\t\tLoading citizen with uuid (%s)...\n", citizenUUIDString);
                loadCitizen(citizen, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCitizen(Citizen citizen, ResultSet resultSet) {
        try {
            String domainUUIDString = resultSet.getString("domain_id");

            if (domainUUIDString != null) {
                UUID domainUUID = UUID.fromString(domainUUIDString);
                Domain domain = Mapper.getDomainFromUUID(domainUUID);
                System.out.println("\t\t\tDomain was found in Mapper: " + (domain != null));
                citizen.setDomain(domain);
            } else {
                System.out.println("\t\t\tDomain uuid is null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDomainsTable(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS Domains(id varchar(255) primary key, name varchar(255), claims text[], citizens text[])";
            statement.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertDomain(Connection connection, String domainUUIDString, String domainName, String domainClaims, String domainCitizens) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO Domains (id, name, claims, citizens) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, domainUUIDString);
            preparedStatement.setString(2, domainName);
            preparedStatement.setString(3, domainClaims);
            preparedStatement.setString(4, domainCitizens);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveDomains(Connection connection) {
        List<Domain> domains = new ArrayList<>(Mapper.getUUIDDomainMap().values());

        for (Domain domain : domains) {
            String domainUUIDString = domain.getUUID().toString();
            String domainName = domain.getName();
            String domainClaims = domain.getClaimsAsString();
            String domainCitizens = domain.getCitizensAsString();
            int domainTierLevel = domain.getDomainTier().getLevel();

            this.insertDomain(connection, domainUUIDString, domainName, domainClaims, domainCitizens);
        }
    }

    private void preloadDomains(Connection connection) {
        try {
            ResultSet resultSet = this.queryTable(connection, "Domains");

            if (resultSet == null) {
                return;
            }

            System.out.println("\tTrying to preload domains...");

            while (resultSet.next()) {
                UUID domainUUID = UUID.fromString(resultSet.getString("id"));
                System.out.printf("\t\tPreloading domain with uuid (%s)\n", domainUUID);
                Domain domain = new Domain(domainUUID);
                Mapper.addDomainWithUUID(domain, domainUUID);
            }
            System.out.println("\tFinished preloading domains!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDomains(Connection connection) {
        try {
            ResultSet resultSet = this.queryTable(connection, "Domains");

            if (resultSet == null) {
                return;
            }

            while (resultSet.next()) {
                UUID domainUUID = UUID.fromString(resultSet.getString("id"));
                Domain domain = Mapper.getDomainFromUUID(domainUUID);
                loadDomain(domain, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadDomain(Domain domain, ResultSet resultSet) {
        try {
            String name = resultSet.getString("name");

            if (name != null) {
                domain.setName(name);
            }

            String claimsString = resultSet.getString("claims");

            if (claimsString != null) {
                String[] claimsStringArray = claimsString.split("\\|");

                for (String chunkCoordinateString : claimsStringArray) {
                    String[] chunkCoordinates = chunkCoordinateString.split(",");
                    int coordX = Integer.parseInt(chunkCoordinates[0]);
                    int coordZ = Integer.parseInt(chunkCoordinates[1]);
                    String worldName = chunkCoordinates[2];
                    World world = Bukkit.getWorld(worldName);
                    boolean worldExists = world != null;

                    if (!worldExists) {
                        continue;
                    }

                    Chunk chunk = world.getChunkAt(coordX, coordZ);
                    Claim claim = new Claim(domain, chunk);

                    domain.addClaim(claim);
                    Mapper.addClaimWithChunk(claim, chunk);
                }
            }

            String citizensString = resultSet.getString("citizens");

            if (citizensString != null) {
                String[] citizensUUIDStrings = citizensString.split("\\|");

                for (String citizenUUIDString : citizensUUIDStrings) {
                    UUID uuid = UUID.fromString(citizenUUIDString);
                    Citizen citizen = citizenManager.getRegisteredCitizen(uuid);
                    domain.addCitizen(citizen);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
