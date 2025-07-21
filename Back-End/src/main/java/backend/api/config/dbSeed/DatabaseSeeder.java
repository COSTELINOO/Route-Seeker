package backend.api.config.dbSeed;

import backend.api.entity.City;
import backend.api.entity.Information;
import backend.api.entity.Location;
import backend.api.repository.CityRepository;
import backend.api.repository.ConnectionRepository;
import backend.api.repository.InformationRepository;
import backend.api.repository.LocationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;
    private final InformationRepository informationRepository;
    private final ConnectionRepository connectionRepository;

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSeeder(CityRepository cityRepository, LocationRepository locationRepository,
                          InformationRepository informationRepository, ConnectionRepository connectionRepository,
                          JdbcTemplate jdbcTemplate ) {
        this.cityRepository = cityRepository;
        this.locationRepository = locationRepository;
        this.informationRepository = informationRepository;
        this.connectionRepository = connectionRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {

        init();
        seedCities(listOfCities());
        seedCitiesInformations(listOfCitiesInformations(citiesDescription()));
        seedIasiLocations(listOfIasiLocations());
        seedIasiConnections(listOfIasiConnections());
        seedLocationsInformations(listOfIasiLocationsInformation());

        System.out.println("Initializarea bazei de date a fost finalizata cu succes!");
    }

    public List<String> loadImages() {

        return        List.of(
     "images/harta-alba-iulia.png",
                "images/harta-arad.png",
                "images/harta-pitesti.png",
                "images/harta-bacau.png",
                "images/harta-oradea.png",
                "images/harta-bistrita.png",
                "images/harta-botosani.png",
                "images/harta-brasov.png",
                "images/harta-braila.png",
                "images/harta-bucuresti.png",
                "images/harta-buzau.png",
                "images/harta-caras-severin.png",
                "images/harta-calarasi.png",
                "images/harta-cluj.png",
                "images/harta-constanta.png",
                "images/sfantu-gheorghe.png",
                "images/harta-targoviste.png",
                "images/harta-craiova.png",
                "images/harta-galati.png",
                "images/harta-giurgiu.png",
                "images/harta-targu-jiu.png",
                "images/harta-miercurea-ciuc.png",
                "images/harta-deva.png",
                "images/harta-alexandria.png",
                "images/harta-iasi.png",
                "images/harta-baia-mare.png",
                "images/harta-dobreta-turnu-severin.png",
                "images/harta-targu-mures.png",
                "images/harta-piatra-neamt.png",
                "images/harta-slatina.png",
                "images/harta-ploiesti.png",
                "images/harta-satu-mare.png",
                "images/harta-zalau.png",
                "images/harta-sibiu.png",
                "images/harta-suceava.png",
                "images/harta-tulcea.png",
                "images/harta-timisoara.png",
                "images/harta-vaslui.png",
                "images/harta-ramnicu-valcea.png",
                "images/harta-focsani.png"
        );

    }

    private List<City> listOfCities() {
        List<String> images=loadImages();
        String path = DatabaseSeeder.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path=path.substring(1,path.length()-1)+'/';
        return List.of(
                new City(null, "Alba Iulia", "AB", path + images.get(0), false, false, 46.077, 23.580),
                new City(null, "Arad", "AR", path + images.get(1), true, true, 46.167, 21.317),
                new City(null, "Pitesti", "AG", path + images.get(2), false, false, 44.757, 24.867),
                new City(null, "Bacau", "BC", path + images.get(3), true, true, 46.600, 26.750),
                new City(null, "Oradea", "BH", path + images.get(4), false, false, 46.867, 21.933),
                new City(null, "Bistrita", "BN", path + images.get(5), false, false, 47.133, 24.500),
                new City(null, "Botosani", "BT", path + images.get(6), true, true, 47.643, 26.669),
                new City(null, "Brasov", "BV", path + images.get(7), true, true, 45.646, 25.208),
                new City(null, "Braila", "BR", path + images.get(8), true, true, 45.269, 27.957),
                new City(null, "Bucuresti", "B", path + images.get(9), true, true, 44.668, 26.1025),
                new City(null, "Buzau", "BZ", path + images.get(10), false, false, 45.151, 26.823),
                new City(null, "Resita", "CS", path + images.get(11), false, false, 45.204, 21.889),
                new City(null, "Calarasi", "CL", path + images.get(12), false, false, 44.350, 27.325),
                new City(null, "Cluj-Napoca", "CJ", path + images.get(13), false, false, 46.592, 23.100),
                new City(null, "Constanta", "CT", path + images.get(14), false, false, 44.673, 28.438),
                new City(null, "Sfantu Gheorghe", "CV", path + images.get(15), false, false, 46.180, 25.600),
                new City(null, "Targoviste", "DB", path + images.get(16), false, false, 44.925, 25.457),
                new City(null, "Craiova", "DJ", path + images.get(17), false, false, 44.117, 23.200),
                new City(null, "Galati", "GL", path + images.get(18), true, true, 45.635, 27.907),
                new City(null, "Giurgiu", "GR", path + images.get(19), false, false, 44.203, 25.969),
                new City(null, "Targu Jiu", "GJ", path + images.get(20), false, false, 44.944, 23.274),
                new City(null, "Miercurea Ciuc", "HR", path + images.get(21), false, false, 46.359, 25.801),
                new City(null, "Deva", "HD", path + images.get(22), false, false, 45.672, 22.902),
                new City(null, "Alexandria", "TR", path + images.get(23), false, false, 43.974, 25.333),
                new City(null, "Iasi", "IS", path + images.get(24), true, false, 47.158, 27.501),
                new City(null, "Baia Mare", "MM", path + images.get(25), false, false, 47.457, 23.568),
                new City(null, "Drobeta-Turnu Severin", "MH", path + images.get(26), false, false, 44.633, 22.656),
                new City(null, "Targu Mures", "MS", path + images.get(27), false, false, 46.351, 24.259),
                new City(null, "Piatra Neamt", "NT", path + images.get(28), false, false, 46.800, 26.373),
                new City(null, "Slatina", "OT", path + images.get(29), false, false, 44.180, 24.372),
                new City(null, "Ploiesti", "PH", path + images.get(30), false, false, 45.042, 26.011),
                new City(null, "Satu Mare", "SM", path + images.get(31), false, false, 47.792, 23.185),
                new City(null, "Zalau", "SJ", path + images.get(32), false, false, 47.000, 23.057),
                new City(null, "Sibiu", "SB", path + images.get(33), false, false, 45.798, 24.125),
                new City(null, "Suceava", "SV", path + images.get(34), true, true, 47.451, 26.055),
                new City(null, "Tulcea", "TL", path + images.get(35), true, true, 45.187, 28.905),
                new City(null, "Timisoara", "TM", path + images.get(36), true, true, 45.653, 21.225),
                new City(null, "Vaslui", "VS", path + images.get(37), true, true, 46.240, 27.728),
                new City(null, "Ramnicu Valcea", "VL", path + images.get(38), false, false, 45.480, 24.375),
                new City(null, "Focsani", "VN", path + images.get(39), false, false, 45.696, 26.800)
        );
    }

    private Map<String, String> citiesDescription() {
        Map<String, String> cityDescriptions = new HashMap<>();
        cityDescriptions.put("Alba Iulia", "Alba Iulia este un oras cu o semnificatie istorica deosebita, fiind locul unde s-a infaptuit Marea Unire din 1918. Cetatea Alba Carolina, una dintre cele mai impresionante fortificatii de tip Vauban din Europa, domina orasul si atrage anual mii de turisti. Orasul imbina perfect istoria cu modernitatea, avand un centru urban renovat si o viata culturala activa.");
        cityDescriptions.put("Arad", "Arad este un oras cu o bogata traditie multiculturala, situat in vestul Romaniei, la granita cu Ungaria. Recunoscut pentru arhitectura sa impresionanta, cladirile de epoca si atmosfera cosmopolita, Arad a fost de-a lungul timpului un important centru comercial si industrial. Astazi, orasul se remarca printr-o viata culturala activa, festivaluri, teatre si parcuri generoase pe malul Muresului.");
        cityDescriptions.put("Pitesti", "Pitesti, resedinta judetului Arges, este un oras dinamic situat in sudul Romaniei, cunoscut pentru industria sa auto, dar si pentru spatiile verzi generoase, precum Parcul Trivale. Pe langa o istorie bogata, orasul ofera acces rapid la zone montane si reprezinta un punct de plecare pentru explorarea Muntilor Fagaras si a Transfagarasanului.");
        cityDescriptions.put("Bacau", "Bacau, situat in inima Moldovei, este un oras cu traditie industriala si culturala. Aflat la confluenta raurilor Bistrita si Siret, Bacau gazduieste numeroase institutii de cultura, parcuri si monumente istorice. De aici se poate ajunge cu usurinta catre statiunile montane si lacurile din zona Moldovei.");
        cityDescriptions.put("Oradea", "Oradea este unul dintre cele mai frumoase orase din vestul tarii, apreciat pentru cladirile sale in stil Secession, baile termale si atmosfera relaxata. Orasul are o istorie multiculturala, cu influente maghiare si austriece, si se remarca printr-un centru istoric recent reabilitat, plin de cafenele si evenimente culturale.");
        cityDescriptions.put("Bistrita", "Bistrita este unul dintre cele mai vechi orase din Transilvania, cu un nucleu medieval bine conservat. Turnul Bisericii Evanghelice domina peisajul urban, iar orasul este inconjurat de dealuri si paduri, fiind poarta de intrare spre Muntii Rodnei si Colibita.");
        cityDescriptions.put("Botosani", "Botosani este un oras moldovenesc cu un bogat patrimoniu istoric si cultural, cunoscut ca loc de nastere al unor mari personalitati precum Mihai Eminescu sau Nicolae Iorga. Orasul este apreciat pentru linistea sa, traditiile folclorice si peisajele rurale autentice din imprejurimi.");
        cityDescriptions.put("Brasov", "Brasovul este una dintre cele mai vizitate destinatii turistice din Romania, situat la poalele Muntilor Carpati. Orasul impresioneaza prin centrul sau medieval, Piata Sfatului, Biserica Neagra si prin accesul rapid la statiunile montane precum Poiana Brasov. Brasovul gazduieste numeroase festivaluri si evenimente culturale pe tot parcursul anului.");
        cityDescriptions.put("Braila", "Braila, port important la Dunare, are o istorie legata de comert si navigatie. Orasul se remarca prin vechile sale cartiere cu case elegante, promenada de pe faleza si atmosfera specifica unui oras-port cu deschidere spre Delta Dunarii.");
        cityDescriptions.put("Bucuresti", "Bucurestiul, capitala si cel mai mare oras al Romaniei, este un centru vibrant, divers si modern. Orasul ofera o multitudine de muzee, teatre, parcuri, restaurante, dar si o arhitectura variata, de la cladiri Belle Epoque la zgarie-nori moderni. Bucurestiul este inima vietii culturale, economice si politice a tarii.");
        cityDescriptions.put("Buzau", "Buzau este orasul de la poalele Subcarpatilor, faimos pentru podgoriile sale, pentru proximitatea Vulcanilor Noroiosi si pentru traditiile culinare locale. Orasul are o comunitate activa si este un punct de plecare pentru drumetii si explorari in Muntii Buzaului.");
        cityDescriptions.put("Resita", "Resita, situata in Banat, este un oras cu o puternica traditie industriala, in special in siderurgie. Inconjurat de dealuri impadurite, orasul ofera oportunitati pentru drumetii, ciclism si activitati in natura, fiind aproape de Parcul National Semenic-Cheile Carasului.");
        cityDescriptions.put("Calarasi", "Calarasi este un oras situat pe malul Dunarii, aproape de granita cu Bulgaria. Este cunoscut pentru industria agricola, dar si pentru zonele naturale de interes, precum Insula Mare a Brailei si rezervatiile de pasari.");
        cityDescriptions.put("Cluj-Napoca", "Cluj-Napoca, capitala neoficiala a Transilvaniei, este un oras universitar dinamic, cu o viata culturala bogata, evenimente internationale, festivaluri de film si muzica. Centrul sau istoric, universitatile si multitudinea de spatii verzi il fac un oras foarte apreciat de tineri si turisti.");
        cityDescriptions.put("Constanta", "Constanta este cel mai mare port la Marea Neagra si unul dintre cele mai vechi orase din Romania, cu o istorie ce dateaza din perioada grecilor si romanilor. Orasul modern ofera plaje, vestigii antice, muzee maritime si o viata de noapte vibranta in sezonul estival.");
        cityDescriptions.put("Sfantu Gheorghe", "Sfantu Gheorghe, resedinta judetului Covasna, este un oras linistit cu o importanta comunitate maghiara. Este punct de plecare spre statiunile balneare din zona si spre peisajele naturale bogate ale tinutului Secuiesc.");
        cityDescriptions.put("Targoviste", "Targoviste a fost capitala domneasca a tarii Romanesti si pastreaza monumente istorice unice, precum Curtea Domneasca, Turnul Chindiei si biserici vechi. Orasul are o importanta deosebita in istoria medievala a Romaniei.");
        cityDescriptions.put("Craiova", "Craiova, cel mai mare oras din Oltenia, este un important centru universitar si cultural. Are numeroase parcuri, muzee si teatre, fiind recunoscut pentru dinamismul sau economic si pentru spiritul sau oltenesc autentic.");
        cityDescriptions.put("Galati", "Galati este un puternic centru industrial si port la Dunare, cu o faleza lunga si panorame deosebite asupra fluviului. Orasul are o istorie legata de comertul pe apa si o comunitate multiculturala.");
        cityDescriptions.put("Giurgiu", "Giurgiu, port important pe Dunare, este poarta de intrare spre Bulgaria, traversata de Podul Prieteniei. Orasul are o istorie strans legata de navigatie si transport fluvial, fiind si un centru agricol.");
        cityDescriptions.put("Targu Jiu", "Targu Jiu este orasul celebrului sculptor Constantin Brancusi, unde se gaseste Ansamblul Monumental cu Coloana Infinitului, Poarta Sarutului si Masa Tacerii. Orasul este situat pe malul Jiului si ofera parcuri generoase si acces la zone montane.");
        cityDescriptions.put("Miercurea Ciuc", "Miercurea Ciuc este resedinta judetului Harghita, faimoasa pentru iernile sale reci, traditiile secuiesti si evenimentele culturale maghiare. Orasul este inconjurat de peisaje montane si izvoare minerale.");
        cityDescriptions.put("Deva", "Deva este situat la poalele dealului ce gazduieste Cetatea Deva, una dintre cele mai bine pastrate fortificatii medievale din Transilvania. Orasul ofera privelisti spectaculoase si acces rapid spre Muntii Apuseni si Parcul Dendrologic Simeria.");
        cityDescriptions.put("Alexandria", "Alexandria este resedinta judetului Teleorman, un oras cu traditie agricola, situat in Campia Romana. Viata linistita, spatiile verzi si apropierea de Dunare ii confera un farmec aparte.");
        cityDescriptions.put("Iasi", "Iasiul, capitala istorica a Moldovei, este un puternic centru universitar, cultural si religios. Palatul Culturii, celebrele biserici, universitatile si evenimentele artistice fac din Iasi un oras fascinant, plin de istorie si energie tanara.");
        cityDescriptions.put("Baia Mare", "Baia Mare este principalul centru urban al Maramuresului, o zona renumita pentru traditiile sale, bisericile de lemn si peisajele montane. Orasul are o bogata mostenire miniera si organizeaza numeroase festivaluri folclorice.");
        cityDescriptions.put("Drobeta-Turnu Severin", "Drobeta-Turnu Severin este un oras-port la Dunare, cu importante vestigii romane, printre care Podul lui Traian si ruinele castrului roman. Orasul ofera acces spre Portile de Fier si peisaje spectaculoase ale defileului Dunarii.");
        cityDescriptions.put("Targu Mures", "Targu Mures este un oras multicultural din centrul Transilvaniei, remarcat pentru arhitectura sa Secession, Palatul Culturii, universitatile bilingve si evenimentele artistice de anvergura.");
        cityDescriptions.put("Piatra Neamt", "Piatra Neamt, supranumit si „Perla Moldovei\", este situat intr-o zona montana pitoreasca, avand telegondola, cetate medievala si acces rapid spre Cheile Bicazului si Lacul Izvorul Muntelui.");
        cityDescriptions.put("Slatina", "Slatina este resedinta judetului Olt, un centru industrial si comercial situat pe malul raului Olt. Orasul este cunoscut pentru podgoriile sale si pentru peisajele de campie si deal.");
        cityDescriptions.put("Ploiesti", "Ploiesti, situat in apropierea Bucurestiului, este un important centru petrolier, cu o traditie indelungata in industria rafinarii. Orasul gazduieste muzee dedicate petrolului si are o viata culturala activa.");
        cityDescriptions.put("Satu Mare", "Satu Mare este un oras multicultural la granita cu Ungaria si Ucraina, caracterizat printr-o arhitectura variata si o atmosfera cosmopolita. Este un important nod de transport si centru economic in nord-vestul tarii.");
        cityDescriptions.put("Zalau", "Zalau, resedinta judetului Salaj, este situat intr-o zona deluroasa, aproape de Gradina Zmeilor si alte rezervatii naturale. Orasul are o istorie veche si o comunitate primitoare.");
        cityDescriptions.put("Sibiu", "Sibiul este unul dintre cele mai frumoase orase medievale din Romania, cu un centru istoric inclus in patrimoniul UNESCO. Renumit pentru festivaluri, muzee si gastronomia locala, Sibiul atrage anual numerosi turisti din tara si strainatate.");
        cityDescriptions.put("Suceava", "Suceava, fosta capitala a Moldovei, este un oras incarcat de istorie, cu o cetate medievala impresionanta, manastiri vechi si traditii bucovinene autentice.");
        cityDescriptions.put("Tulcea", "Tulcea este poarta principala de acces spre Delta Dunarii, un oras cu o atmosfera multiculturala si peisaje unice. De aici pornesc excursii catre canalele, lacurile si satele din Delta Dunarii.");
        cityDescriptions.put("Timisoara", "Timisoara este un centru universitar si multicultural din vestul tarii, cunoscut ca orasul Revolutiei din 1989. Pietele largi, cladirile istorice, evenimentele artistice si atmosfera vest-europeana fac din Timisoara un loc special.");
        cityDescriptions.put("Vaslui", "Vaslui este un oras moldovenesc cu o istorie veche, legata de domnia lui Stefan cel Mare si batalia de la Podul Inalt. Orasul este inconjurat de peisaje agricole si sate traditionale.");
        cityDescriptions.put("Ramnicu Valcea", "Ramnicu Valcea este situat la poalele Carpatilor, fiind un important centru industrial si balnear. Orasul ofera acces spre statiunile Olanesti si Calimanesti-Caciulata, precum si catre trasee montane.");
        cityDescriptions.put("Focsani", "Focsani este cunoscut pentru podgoriile sale celebre, pentru traditiile vinicole si pentru rolul sau in istoria Unirii Principatelor Romane. Orasul se afla la granita dintre Moldova si Muntenia.");

        return cityDescriptions;
    }

    private List<Information>listOfCitiesInformations(Map<String,String> info) throws Exception {
        List <Information>list=new ArrayList<>();
        for(Map.Entry<String,String> iter:info.entrySet())
        {
            City city=cityRepository.findName(iter.getKey()).orElse(new City());
            Information information=new Information();
            information.setIdCity(city.getId());
            information.setDescription(iter.getValue());
            information.setType("Oras");
            information.setDangerZone(city.getExist());
            information.setCity(true);
            list.add(information);
        }
        return list;
    }

    private List<Information> listOfIasiLocationsInformation() {
        return List.of(
                new Information(null, 1L, null, "Gara principala a orasului Iasi, punct important de transport feroviar regional si national.", "gara", false, false),
                new Information(null, 2L, null, "Palat emblematic al Iasului, gazduieste muzee si evenimente culturale.", "muzeu", false, false),
                new Information(null, 3L, null, "Zona Copou este cunoscuta pentru Parcul Copou, Gradina Botanica si Universitatea Alexandru Ioan Cuza.", "zona rezidentiala", false, false),
                new Information(null, 4L, null, "Cartier rezidential si zona de tranzit din sudul orasului Iasi.", "cartier", false, false),
                new Information(null, 5L, null, "Partea nordica a cartierului Tatarasi, cu multe spatii verzi si locuinte.", "cartier", false, false),
                new Information(null, 6L, null, "Cartier situat in partea de vest a orasului, cunoscut pentru accesul facil catre iesirea din oras si zona industriala.", "cartier", false, false),
                new Information(null, 7L, null, "Interesectie si cartier central, aproape de Universitatea Tehnica si de principalele artere ale orasului.", "zona centrala", false, false),
                new Information(null, 8L, null, "Cartier rezidential mare, cu multe blocuri si parcuri, situat in nord-vestul orasului.", "cartier", false, false),
                new Information(null, 9L, null, "Cartier popular, cu acces rapid la centrul orasului si la Gara Iasi.", "cartier", false, false),
                new Information(null, 10L, null, "Zona rezidentiala in partea de sud a orasului, cunoscuta pentru liniste si acces la transport in comun.", "cartier", false, false),
                new Information(null, 11L, null, "Plaja urbana moderna, populara in sezonul cald, cu facilitati pentru relaxare si sport.", "plaja urbana", false, false),
                new Information(null, 12L, null, "Cartier situat in partea de est a orasului, cu acces la zone industriale si comerciale.", "cartier", false, false),
                new Information(null, 13L, null, "Zona rezidentiala si industriala in sud-vestul Iasului, cu vedere spre dealurile din imprejurimi.", "cartier", false, false)
        );
    }

    private List<Location>listOfIasiLocations()
    {
        return List.of(
                new Location(null, 25L, "Gara Iasi", 47.1670, 27.5719, true, false),
                new Location(null, 25L, "Palatul Culturii", 47.1585, 27.6014, false, false),
                new Location(null, 25L, "Copou", 47.1879, 27.5740, false, false),
                new Location(null, 25L, "Tatarasi Sud", 47.1450, 27.6600, false, false),
                new Location(null, 25L, "Tatarasi Nord", 47.1700, 27.6250, false, false),
                new Location(null, 25L, "Pacurari", 47.1820, 27.5459, false, false),
                new Location(null, 25L, "Podu Ros", 47.1428, 27.6012, false, false),
                new Location(null, 25L, "Dacia", 47.1692, 27.5499, false, false),
                new Location(null, 25L, "Alexandru cel Bun", 47.1602, 27.5599, false, false),
                new Location(null, 25L, "Nicolina 1", 47.1252, 27.5851, false, false),
                new Location(null, 25L, "Tiki Beach", 47.1688, 27.6541, false, false),
                new Location(null, 25L, "Bularga", 47.1230, 27.6350, false, false),
                new Location(null, 25L, "Galata", 47.1380, 27.5765, false, true));
    }

    private List<backend.api.entity.Connection> listOfIasiConnections()
    {
        return List.of(
                new backend.api.entity.Connection(null,1L,2L),
                new backend.api.entity.Connection(null,2L,3L),
                new backend.api.entity.Connection(null,3L,4L),
                new backend.api.entity.Connection(null,4L,5L),
                new backend.api.entity.Connection(null,5L,6L),
                new backend.api.entity.Connection(null,6L,7L),
                new backend.api.entity.Connection(null,7L,8L),
                new backend.api.entity.Connection(null,8L,9L),
                new backend.api.entity.Connection(null,9L,10L),
                new backend.api.entity.Connection(null,10L,11L),
                new backend.api.entity.Connection(null,11L,12L),
                new backend.api.entity.Connection(null,12L,13L),
                new backend.api.entity.Connection(null,13L,1L),
                new backend.api.entity.Connection(null,10L,1L),
                new backend.api.entity.Connection(null,7L,13L)
        );
    }

    private void seedCities(List<City> cities) throws Exception {
        for(City c : cities) {
            cityRepository.save(c);
        }
    }

    private void seedLocationsInformations(List<Information> locations) throws Exception {
        for(Information location : locations) {
            informationRepository.save(location);
        }
    }

    private void seedCitiesInformations(List<Information> informations) throws Exception {
        for(Information c : informations) {
            informationRepository.save(c);
        }
    }

    private void seedIasiLocations(List<Location> locations) throws Exception {
        for(Location location : locations) {
            locationRepository.save(location);
        }
    }

    private void seedIasiConnections(List<backend.api.entity.Connection> connections) throws Exception {
        for(backend.api.entity.Connection connection : connections) {
            connectionRepository.save(connection);
        }
    }

  
    public void init() throws Exception {
        try (Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Initializare obiecte PL/SQL...");

            System.out.println("Sterg si creez tabelele necesare...");

            stmt.execute("DROP TABLE IF EXISTS informations CASCADE");
            stmt.execute("DROP TABLE IF EXISTS connections CASCADE");
            stmt.execute("DROP TABLE IF EXISTS locations CASCADE");
            stmt.execute("DROP TABLE IF EXISTS cities CASCADE");

            stmt.execute("CREATE TABLE IF NOT EXISTS users " +
                    "(" +
                    "    id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                    "    username VARCHAR(50) NOT NULL UNIQUE," +
                    "    password VARCHAR(255) NOT NULL" +
                    ")");

            stmt.execute("CREATE TABLE cities " +
                    "(" +
                    "    id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                    "    name VARCHAR(50) NOT NULL UNIQUE," +
                    "    cod VARCHAR(4) NOT NULL UNIQUE," +
                    "    image TEXT," +
                    "    exist BOOLEAN NOT NULL DEFAULT FALSE," +
                    "    random BOOLEAN NOT NULL DEFAULT FALSE," +
                    "    poz_x REAL NOT NULL," +
                    "    poz_y REAL NOT NULL" +
                    ")");

            stmt.execute("CREATE TABLE locations " +
                    "(" +
                    "    id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                    "    id_city INTEGER NOT NULL," +
                    "    name VARCHAR(50) NOT NULL UNIQUE," +
                    "    poz_x REAL NOT NULL," +
                    "    poz_y REAL NOT NULL," +
                    "    st BOOLEAN DEFAULT FALSE," +
                    "    fi BOOLEAN DEFAULT FALSE," +
                    "    FOREIGN KEY (id_city) REFERENCES cities(id)" +
                    ")");

            stmt.execute("CREATE TABLE connections " +
                    "(" +
                    "    id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                    "    id_int INTEGER NOT NULL," +
                    "    id_ext INTEGER NOT NULL," +
                    "    FOREIGN KEY (id_int) REFERENCES locations(id)," +
                    "    FOREIGN KEY (id_ext) REFERENCES locations(id)" +
                    ")");

            stmt.execute("CREATE TABLE informations " +
                    "(" +
                    "    id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                    "    id_location INTEGER UNIQUE," +
                    "    id_city INTEGER UNIQUE," +
                    "    description VARCHAR(2500) NOT NULL UNIQUE," +
                    "    type VARCHAR(200) NOT NULL," +
                    "    danger_zone BOOLEAN NOT NULL DEFAULT FALSE," +
                    "    city BOOLEAN NOT NULL DEFAULT FALSE," +
                    "    FOREIGN KEY (id_location) REFERENCES locations(id)," +
                    "    FOREIGN KEY (id_city) REFERENCES cities(id)" +
                    ")");

            System.out.println("Sterg obiectele PL/SQL existente...");

            stmt.execute("DROP VIEW IF EXISTS v_location_details CASCADE");
            stmt.execute("DROP VIEW IF EXISTS v_connections_details CASCADE");
            stmt.execute("DROP VIEW IF EXISTS v_cities_details CASCADE");

            stmt.execute("DROP FUNCTION IF EXISTS find_shortest_path CASCADE");
            stmt.execute("DROP FUNCTION IF EXISTS find_longest_path CASCADE");
            stmt.execute("DROP FUNCTION IF EXISTS find_minimum_cycle CASCADE");
            stmt.execute("DROP FUNCTION IF EXISTS generate_route_report CASCADE");

            stmt.execute("DROP FUNCTION IF EXISTS update_city_exist() CASCADE");
            stmt.execute("DROP FUNCTION IF EXISTS check_start_end_uniqueness() CASCADE");

            stmt.execute("DROP TRIGGER IF EXISTS after_location_insert ON locations");
            stmt.execute("DROP TRIGGER IF EXISTS before_location_update ON locations");
            stmt.execute("DROP TRIGGER IF EXISTS before_location_insert ON locations");
            stmt.execute("DROP TRIGGER IF EXISTS before_location_update_with_messages ON locations");
            stmt.execute("DROP TRIGGER IF EXISTS before_location_insert_with_messages ON locations");

            System.out.println("Creere view-uri...");

            String viewLocationDetails =
                    "CREATE OR REPLACE VIEW v_location_details AS " +
                            "SELECT " +
                            "    l.id, l.name, l.poz_x, l.poz_y, l.st, l.fi, " +
                            "    c.name AS city_name, c.cod AS city_code, " +
                            "    ci.description AS city_description, ci.type AS city_type, " +
                            "    i.description, i.type, i.danger_zone " +
                            "FROM locations l " +
                            "JOIN cities c ON l.id_city = c.id " +
                            "LEFT JOIN informations i ON l.id = i.id_location "+
                            "LEFT JOIN informations ci ON ci.id_city = c.id AND ci.city = TRUE";

            stmt.execute(viewLocationDetails);

            String viewConnectionDetails =
                    "CREATE OR REPLACE VIEW v_connections_details AS " +
                            "SELECT " +
                            "    c.id, " +
                            "    l1.name AS from_location, " +
                            "    l2.name AS to_location, " +
                            "    c1.name AS from_city, " +
                            "    c2.name AS to_city " +
                            "FROM connections c " +
                            "JOIN locations l1 ON c.id_int = l1.id " +
                            "JOIN locations l2 ON c.id_ext = l2.id " +
                            "JOIN cities c1 ON l1.id_city = c1.id " +
                            "JOIN cities c2 ON l2.id_city = c2.id";
            stmt.execute(viewConnectionDetails);

            String viewCitiesDetails =
                    "CREATE OR REPLACE VIEW v_cities_details AS " +
                            "SELECT " +
                            "    c.id AS city_id, " +
                            "    c.name AS city_name, " +
                            "    c.cod AS city_code, " +
                            "    c.exist, " +
                            "    c.random, " +
                            "    c.poz_x, " +
                            "    c.poz_y, " +
                            "    COALESCE(i.description, 'Fara descriere') AS description, " +
                            "    COALESCE(i.type, 'Oras') AS type, " +
                            "    COALESCE(i.danger_zone, FALSE) AS danger_zone " +
                            "FROM cities c " +
                            "LEFT JOIN informations i ON c.id = i.id_city AND i.city = TRUE " +
                            "ORDER BY c.name";
            stmt.execute(viewCitiesDetails);

            System.out.println("Creez triggere...");



            String triggerUpdateCityExist =
                    "CREATE OR REPLACE FUNCTION update_city_exist() RETURNS TRIGGER AS $$ " +
                            "BEGIN " +
                            "    UPDATE cities SET exist = TRUE WHERE id = NEW.id_city; " +
                            "    RETURN NEW; " +
                            "END; " +
                            "$$ LANGUAGE plpgsql; " +
                            " " +
                            "CREATE TRIGGER after_location_insert " +
                            "AFTER INSERT ON locations " +
                            "FOR EACH ROW " +
                            "EXECUTE FUNCTION update_city_exist()";
            stmt.execute(triggerUpdateCityExist);

            String triggerCheckStartEndUniqueness =
                    "CREATE OR REPLACE FUNCTION check_start_end_uniqueness() RETURNS TRIGGER AS $$ " +
                            "DECLARE " +
                            "    start_count INTEGER; " +
                            "    end_count INTEGER; " +
                            "BEGIN " +
                            "    IF NEW.st = TRUE THEN " +
                            "        SELECT COUNT(*) INTO start_count FROM locations WHERE st = TRUE AND id != NEW.id; " +
                            "        IF start_count > 0 THEN " +
                            "            UPDATE locations SET st = FALSE WHERE id != NEW.id AND st = TRUE; " +
                            "            RAISE NOTICE 'Alte locatii de start au fost dezactivate'; " +
                            "        END IF; " +
                            "    END IF; " +
                            "    " +
                            "    IF NEW.fi = TRUE THEN " +
                            "        SELECT COUNT(*) INTO end_count FROM locations WHERE fi = TRUE AND id != NEW.id; " +
                            "        IF end_count > 0 THEN " +
                            "            UPDATE locations SET fi = FALSE WHERE id != NEW.id AND fi = TRUE; " +
                            "            RAISE NOTICE 'Alte locatii de final au fost dezactivate'; " +
                            "        END IF; " +
                            "    END IF; " +
                            "    " +
                            "    RETURN NEW; " +
                            "END; " +
                            "$$ LANGUAGE plpgsql; " +
                            " " +
                            "CREATE TRIGGER before_location_update " +
                            "BEFORE UPDATE ON locations " +
                            "FOR EACH ROW " +
                            "EXECUTE FUNCTION check_start_end_uniqueness(); " +
                            " " +
                            "CREATE TRIGGER before_location_insert " +
                            "BEFORE INSERT ON locations " +
                            "FOR EACH ROW " +
                            "EXECUTE FUNCTION check_start_end_uniqueness()";
            stmt.execute(triggerCheckStartEndUniqueness);
            System.out.println("Obiecte PL/SQL create cu succes!");

        } catch (SQLException e) {
            throw new Exception("Eroare la initializarea obiectelor PL/SQL" +e.getMessage());
        }
    }

}