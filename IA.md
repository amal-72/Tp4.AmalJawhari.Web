# Utilisation de l'intelligence artificielle (IA)

Pendant la réalisation du TP4 Web RAG, j'ai utilisé l'intelligence artificielle ponctuellement pour m'aider à comprendre certaines fonctionnalités de LangChain4j, résoudre des difficultés techniques et mieux maîtriser les composants intégrés dans mon application Jakarta EE.

---

## Utilisation 1 : Routage des questions avec LanguageModelQueryRouter

### 1. Problème rencontré

Dans mon application, j'utilise deux fichiers PDF pour le RAG : un document consacré à l'intelligence artificielle et au RAG, et un autre consacré aux réseaux informatiques.

Je voulais comprendre comment le système pouvait choisir le document adapté à une question posée par l'utilisateur.

### 2. Raison de l'utilisation de l'IA

J'ai utilisé l'IA pour comprendre le rôle de `LanguageModelQueryRouter` et son fonctionnement avec plusieurs retrievers associés à des documents différents.

### 3. Prompt utilisé

« Comment fonctionne LanguageModelQueryRouter dans LangChain4j lorsqu'on utilise plusieurs ContentRetriever associés à différents documents ? Comment le modèle choisit-il le retriever adapté à une question ? »

### 4. Réponse de l'IA

`LanguageModelQueryRouter` utilise un modèle de langage pour analyser une question et déterminer quel ou quels retrievers sont les plus pertinents.

Chaque retriever est associé à une description de son contenu. Le modèle utilise ces descriptions pour orienter la recherche documentaire vers les sources adaptées à la question.

### 5. Ce que j'ai appris

J'ai compris l'intérêt d'associer une description à chaque retriever afin de permettre au modèle de sélectionner les documents pertinents. Cela permet d'organiser la recherche lorsque plusieurs fichiers PDF sont utilisés.

---

## Utilisation 2 : Transformation des questions avec CompressingQueryTransformer

### 1. Problème rencontré

Lors d'une conversation avec un assistant, certaines questions peuvent dépendre du contexte des messages précédents. Une question courte ou imprécise peut donc être difficile à comprendre lorsqu'elle est considérée indépendamment.

### 2. Raison de l'utilisation de l'IA

J'ai utilisé l'IA pour comprendre comment `CompressingQueryTransformer` permet de transformer une question avant de lancer la recherche documentaire.

### 3. Prompt utilisé

« Quel est le rôle de CompressingQueryTransformer dans LangChain4j ? Comment transforme-t-il une question avant la recherche dans un système RAG ? »

### 4. Réponse de l'IA

`CompressingQueryTransformer` utilise un modèle de langage pour reformuler une question en tenant compte du contexte conversationnel disponible.

Il permet notamment de transformer une question dépendante des échanges précédents en une requête plus autonome et compréhensible pour le système de recherche.

### 5. Ce que j'ai appris

J'ai compris que la transformation des questions peut améliorer la qualité des recherches documentaires, notamment lorsque l'utilisateur utilise des questions courtes ou fait référence à des éléments mentionnés précédemment.

---

## Utilisation 3 : Gestion de la mémoire conversationnelle avec LangChain4j

### 1. Problème rencontré

Je voulais permettre à mon assistant Web de conserver le contexte des échanges précédents afin que l'utilisateur puisse poser des questions complémentaires sans devoir répéter toutes les informations.

### 2. Raison de l'utilisation de l'IA

J'ai utilisé l'IA pour comprendre le fonctionnement de `MessageWindowChatMemory` et le rôle du paramètre `maxMessages`.

### 3. Prompt utilisé

« Comment fonctionne MessageWindowChatMemory dans LangChain4j ? À quoi sert withMaxMessages(20) et comment cette mémoire conserve-t-elle le contexte d'une conversation ? »

### 4. Réponse de l'IA

`MessageWindowChatMemory` permet de conserver les derniers messages d'une conversation.

La méthode `withMaxMessages(20)` définit une limite de 20 messages. Lorsque cette limite est dépassée, les anciens messages sont supprimés afin de conserver une fenêtre de conversation récente.

Cette mémoire permet au modèle de prendre en compte les échanges précédents pour produire des réponses cohérentes avec le contexte.

### 5. Ce que j'ai appris

J'ai compris comment conserver un historique limité des conversations et comment cette mémoire permet à l'assistant de répondre à des questions complémentaires en tenant compte des échanges précédents.

---

## Utilisation 4 : Compréhension du fonctionnement des retrievers

### 1. Problème rencontré

Dans mon application, j'utilise deux magasins d'embeddings associés à deux documents PDF différents. Je voulais comprendre le rôle des `ContentRetriever` et l'influence des paramètres `maxResults` et `minScore`.

### 2. Raison de l'utilisation de l'IA

J'ai utilisé l'IA pour comprendre comment les retrievers récupèrent les segments pertinents et comment leurs paramètres influencent les résultats de la recherche.

### 3. Prompt utilisé

« Quel est le rôle de EmbeddingStoreContentRetriever dans LangChain4j ? Quelle est la différence entre maxResults et minScore ? »

### 4. Réponse de l'IA

`EmbeddingStoreContentRetriever` permet de rechercher les segments de documents les plus proches de l'embedding d'une question.

- `maxResults` définit le nombre maximal de segments à récupérer.
- `minScore` définit le seuil minimal de similarité qu'un segment doit atteindre pour être retenu.

Ces paramètres permettent de contrôler la quantité et la pertinence des informations récupérées.

### 5. Ce que j'ai appris

J'ai compris comment les retrievers effectuent la recherche dans les magasins d'embeddings et comment ajuster les paramètres pour contrôler les résultats retournés au modèle de langage.

---

## Conclusion

Ces différentes utilisations de l'intelligence artificielle m'ont permis de mieux comprendre les fonctionnalités avancées de LangChain4j intégrées dans mon application Web.

J'ai approfondi le fonctionnement du routage des questions, de leur transformation avant la recherche documentaire, de la mémoire conversationnelle et des mécanismes de récupération des informations.

Ces explications m'ont aidée à mieux comprendre mon code, à interpréter les paramètres utilisés et à maîtriser davantage les composants mis en place dans le cadre du TP4.

J'ai retenu qu'il est important de comprendre les mécanismes utilisés et pas seulement d'obtenir un résultat à l'aide de l'intelligence artificielle.