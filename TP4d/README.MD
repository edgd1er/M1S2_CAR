# M1S2_CAR

Conception application répartie

Implémentation d'une application web mettant en oeuvre la persistance via l'utilisation du framework Entrepise Java Bean d'oracle. 

Le point 1 a 4 sont developpés et fonctionnels.
Le point 5 est développés, malheureusement un élément empèche la transaction de s'effectuer et l'enregistrement de l'objet FinalizedCartEntity ne peut se faire. 


28/03/2015

*** 0/ README

ce projet permet a un utilisateur:
- de vider le contenu de la BDD des livres.
- d'initialiser la bdd avec 5 livres
- d'ajouter des livres de son choix
- de mettre/enlever les livres de sont panier
- de consulter le contenu du panier
- ( de procéder au paiement et de consulter son bon de commande ) non fonctionnel.


Le war est disponible dans le repertoire dist. Il est possible alors de déployer l'application sur tout serveur d'applications.

Ici, nous avons utilisé Glashfish 4.01.

*** 1/ Design
La javadoc propose une description détaillée de l'utilisation des objets.

ce package permet la gestion des livres ( ajout, retrait, consultation). Toutes les manipulations sont sauvegardées en base de donnée.
Le package Book contient le Java Bean BookSessionBean qui implement l'interface local BookSessionBeanItfLocal pour gérer les livres représentés par l'entité BookEntity.
La persistance est défini dans le fichier persistance.xml et les informations nécéssaires ) la connexion dans le fichier glassfish-ressources.xml.

Le package cart correspond à la phase d'ajout/retrait d'objet du panier par le Java bean BookSessionBean qui implemente l'interface local BookSessionBeanItfLocal et manipule le panier via le CartEntity.

Le package servlet subdivisé en book et cart expose les pages web qui seront disponibles pour les utilisateurs.

Enfin la classe toolsServlet est un classe utilitaire qui sert a générer le code html (vérification du code produit par les tests unitaires devient donc possible.)


*** 2/ Erreurs / Exception

quand une erreur simple ne nécéssitait pas d'avantage d'explication, un simple booleen a été mis en place.

public boolean add(String book2buy) {
        if (book2buy == null) {
            return false;
        }
        if (book2buy.isEmpty()) {
            return false;
        }
        booksId.add(book2buy);
        return true;
    }

Dans les cas plus déliquats, une exception est utilisée afin de recueillir des informations sur les causes du problème rencontré.


 public void removeBook(BookEntity be) throws Exception {
        if (be == null) {
            Exception e = new Exception("Error, Parameter cannot be null.");
            throw e;
        }
        this.books.remove(be);
    }

*** 3/ Architecture

png disponible dans le repertoire.

*** 4/ Interface

Utilisation des interfaces locales pour les beans sessions:
CartSessionBeanItfLocal
BookSessionBeanItfLocal

*** 5/ Exemple de code


Définition d'une entité, de la table associé, des requetes pré enregistrées et des noms de champs via les annotations.:

@Entity
@Table(name = "book")
@NamedQueries({
    @NamedQuery(name = "books.getallbooks", query = "select object(b) from BookEntity b"),
    @NamedQuery(name = "books.deleteall", query = "delete from BookEntity b"),
    @NamedQuery(name = "books.findByTitle", query = "SELECT b FROM BookEntity b WHERE b.bookTitle LIKE :title"),
    @NamedQuery(name = "books.AllAuthors", query = "SELECT distinct b.bookAuthor FROM BookEntity b"),
    @NamedQuery(name = "books.findByAuthor", query = "SELECT b FROM BookEntity b WHERE b.bookAuthor = :author")
}
)
public class BookEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "title", nullable = false)
    private String bookTitle;
    @Column(name = "author", nullable = false)
    private String bookAuthor;
    @Column(name = "book_year", nullable = false)
    private int bookYear;


Une utilisation d'un table d'association a été paramétrée pour lier les achats aux utilisateurs.

@ManyToMany
    @JoinTable(name = "Order_Items", joinColumns
            = @JoinColumn(name = "OI_ORDER_ID", referencedColumnName = "ORDER_ID"),
            inverseJoinColumns
            = @JoinColumn(name = "OI_BOOK_ID", referencedColumnName = "title")
    )
    protected Set<book.BookEntity> books;



Definition d'un session Bean, en mode stateful, avec des transactions gérées a chaque opération.
Sont egalement définis, l'unité de persistance utilisée, l'entity manager qui
 permettra de manipuler les entités et la ressource de gestion des transactions.


@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class BookSessionBean implements BookSessionBeanItfLocal {

    @PersistenceContext(unitName = "TP4dPU")
    private EntityManager bem;

    @Resource
    private UserTransaction ut;


Exemple d'utilisation d'un bean session.

la récuperation d'un Bean session se fait dans la classe qui l'utilise via une annotation

   @EJB
    private BookSessionBeanItfLocal myBookBean;


myBookBean.createBook(titre,author, iyear) appelle 


@Override
    public String createBook(String Title, String Author, int iYear) {
......

  try {
                    ut.begin();
                    BookEntity book = new BookEntity(Title, Author, iYear);
                    bem.persist(book);
                    ut.commit();
                    ret = "";
                } catch (Exception e) {
                    ret = "Error while creating book named " + Title + " written by " + Author + " in "
                            + String.valueOf(iYear) + ": " + e.getMessage() + "<br>";
                }


Les fichiers JSP ont été utilisé pour tirer parti de leurs interaction avec le framework JEE.

Dans le cas présent, on récupère des paramètres de requétes pour les placer dans la session et pouvoir les utiliser ultérieurement.

  <% //adding parameters to session for future use
            session.setAttribute("year", request.getParameter("year"));
            session.setAttribute("title", request.getParameter("title"));
            session.setAttribute("author", request.getParameter("author"));
        %>

des inclusions et forward ont été utilisé pour pouvoir réutiliser des pages ou faciliterle passage de paramètres:

        <%response.setHeader("Refresh", "15; URL=formulaire.jsp");%>
        <!-- example of jsp include page with parameters send in addition -->
        <jsp:include page="formulaire.jsp">
            <jsp:param name="title" value="<%=request.getParameter("title")%>"/>
            <jsp:param name="author" value="<%=request.getParameter("author")%>"/>
            <jsp:param name="year" value="<%=request.getParameter("year")%>"/>
        </jsp:include>