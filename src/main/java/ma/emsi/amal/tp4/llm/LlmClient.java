ear
        package ma.emsi.amal.tp4.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.service.AiServices;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Dependent
public class LlmClient implements Serializable {

    private static final long serialVersionUID = 1L;

    private Assistant assistant;
    private ChatMemory chatMemory;

    @Inject
    public LlmClient(RagService ragService) {

        // Création du modèle Gemini
        String apiKey = System.getenv("GEMINI_KEY");

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .build();

        // Mémoire de conversation
        chatMemory = MessageWindowChatMemory.withMaxMessages(20);

        // Retriever du premier document : rag.pdf
        ContentRetriever ragRetriever =
                EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(ragService.getRagStore())
                        .embeddingModel(ragService.getEmbeddingModel())
                        .maxResults(5)
                        .minScore(0.5)
                        .build();

        // Retriever du deuxième document : reseaux.pdf
        ContentRetriever reseauxRetriever =
                EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(ragService.getReseauxStore())
                        .embeddingModel(ragService.getEmbeddingModel())
                        .maxResults(10)
                        .minScore(0.0)
                        .build();

        // Association des retrievers à leurs descriptions
        Map<ContentRetriever, String> retrievers = new LinkedHashMap<>();

        retrievers.put(
                ragRetriever,
                "Document contenant le cours sur l'intelligence artificielle, "
                        + "le RAG et les techniques de génération augmentée."
        );

        retrievers.put(
                reseauxRetriever,
                "Document contenant des informations sur les réseaux informatiques, "
                        + "notamment les catégories LAN, WAN et Internet."
        );

        // Test 2 : transformation de la question
        CompressingQueryTransformer queryTransformer =
                new CompressingQueryTransformer(model);

        // Test 3 : routage entre les deux documents
        LanguageModelQueryRouter queryRouter =
                new LanguageModelQueryRouter(model, retrievers);

        // Configuration du processus RAG
        RetrievalAugmentor retrievalAugmentor =
                DefaultRetrievalAugmentor.builder()
                        .queryTransformer(queryTransformer)
                        .queryRouter(queryRouter)
                        .build();

        // Création de l'assistant RAG
        assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    // Définition du rôle système
    public void setSystemRole(String systemRole) {
        chatMemory.clear();
        chatMemory.add(SystemMessage.from(systemRole));
    }

    // Envoi d'une question à l'assistant
    public String chat(String prompt) {
        return assistant.chat(prompt);
    }
}
