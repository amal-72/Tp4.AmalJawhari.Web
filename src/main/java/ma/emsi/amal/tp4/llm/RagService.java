
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

        System.out.println("Début de l'ingestion RAG...");

        // Vérification de la clé API Gemini
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "La variable d'environnement GEMINI_KEY est introuvable."
            );
        }

        // Création du modèle d'embeddings multilingue
        embeddingModel = GoogleGenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-embedding-001")
                .outputDimensionality(768)
                .build();

        // Ingestion des deux documents PDF
        ragStore = createEmbeddingStore("documents/rag.pdf");
        reseauxStore = createEmbeddingStore("documents/reseaux.pdf");

        System.out.println("Ingestion des deux documents terminée.");
    }

    /**
     * Charge un PDF, le découpe en segments,
     * génère les embeddings et les enregistre.
     */
    private EmbeddingStore<TextSegment> createEmbeddingStore(String fileName) {

        // Chargement du document PDF
        Document document = ClassPathDocumentLoader.loadDocument(
                fileName,
                new ApacheTikaDocumentParser()
        );

        // Découpage du document en segments
        List<TextSegment> segments =
                DocumentSplitters.recursive(500, 100).split(document);

        System.out.println("Document chargé : " + fileName);
        System.out.println("Nombre de segments créés : " + segments.size());

        // Création du magasin d'embeddings
        EmbeddingStore<TextSegment> embeddingStore =
                new InMemoryEmbeddingStore<>();

        // Génération et enregistrement des embeddings
        embeddingStore.addAll(
                embeddingModel.embedAll(segments).content(),
                segments
        );

        System.out.println("Embeddings enregistrés pour : " + fileName);

        return embeddingStore;
    }

    /**
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

    /**
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
