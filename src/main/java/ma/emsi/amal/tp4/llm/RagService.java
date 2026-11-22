package ma.emsi.amal.tp4.llm;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;

import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RagService {

    private EmbeddingModel embeddingModel;

    private EmbeddingStore<TextSegment> ragStore;
    private EmbeddingStore<TextSegment> reseauxStore;

    @PostConstruct
    public void init() {

        System.out.println("====================================");
        System.out.println("Début de l'ingestion RAG...");
        System.out.println("====================================");

        // Vérification de la clé API Gemini
        String apiKey = System.getenv("clegemini");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "La variable d'environnement GEMINI_KEY est introuvable."
            );
        }

        System.out.println("Clé API Gemini détectée.");

        // Création du modèle d'embeddings multilingue
        System.out.println("Initialisation du modèle d'embeddings...");

        embeddingModel = GoogleGenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-embedding-001")
                .outputDimensionality(768)
                .build();

        System.out.println("Modèle d'embeddings initialisé.");

        // Ingestion des deux documents PDF
        ragStore = createEmbeddingStore("documents/rag.pdf");
        reseauxStore = createEmbeddingStore("documents/reseaux.pdf");

        System.out.println("====================================");
        System.out.println("Ingestion des deux documents terminée.");
        System.out.println("====================================");
    }

    /*
     * Charge un PDF, le découpe en segments,
     * génère les embeddings et les enregistre.
     */
    private EmbeddingStore<TextSegment> createEmbeddingStore(String fileName) {

        try {

            System.out.println("\nChargement du document : " + fileName);

            // Chargement du document PDF
            Document document = ClassPathDocumentLoader.loadDocument(
                    fileName,
                    new ApacheTikaDocumentParser()
            );

            System.out.println("Document chargé : " + fileName);

            // Découpage du document en segments
            List<TextSegment> segments =
                    DocumentSplitters.recursive(500, 100).split(document);

            System.out.println(
                    "Nombre de segments créés : " + segments.size()
            );

            // Création du magasin d'embeddings
            System.out.println("Étape 1 : création du magasin d'embeddings.");

            EmbeddingStore<TextSegment> embeddingStore =
                    new InMemoryEmbeddingStore<>();

            System.out.println("Étape 2 : magasin créé.");

            // Génération des embeddings
            System.out.println(
                    "Étape 3 : début de génération des embeddings pour "
                            + fileName
            );

            var response = embeddingModel.embedAll(segments);

            System.out.println(
                    "Étape 4 : embeddings générés : "
                            + response.content().size()
            );

            // Enregistrement des embeddings et des segments
            embeddingStore.addAll(response.content(), segments);

            System.out.println(
                    "Étape 5 : embeddings enregistrés pour : " + fileName
            );

            return embeddingStore;

        } catch (Exception e) {

            System.err.println(
                    "ERREUR pendant l'ingestion du document : " + fileName
            );

            e.printStackTrace();

            throw new RuntimeException(
                    "Échec de l'ingestion du document " + fileName,
                    e
            );
        }
    }

    /*
     * Retriever du document sur le RAG et l'intelligence artificielle.
     */
    public ContentRetriever getRagRetriever() {

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(ragStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.0)
                .build();
    }

    /*
     * Retriever du document sur les réseaux informatiques.
     */
    public ContentRetriever getReseauxRetriever() {

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(reseauxStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.0)
                .build();
    }

    // Accès aux magasins d'embeddings

    public EmbeddingStore<TextSegment> getRagStore() {
        return ragStore;
    }

    public EmbeddingStore<TextSegment> getReseauxStore() {
        return reseauxStore;
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }
}
