# Good Snowman Is Hard To Build

Jogo de puzzle em **Java + JavaFX**, inspirado no *A Good Snowman Is Hard To Build*. Controlas um monstro que empurra bolas de neve pelo tabuleiro para construir um boneco de neve completo.

Projeto de Programação Orientada a Objetos 2 (PO2), IPBeja.

## Objetivo

Empilhar três bolas de neve (**grande → média → pequena**) na mesma posição para formar um boneco de neve.

## Como jogar

| Tecla / botão | Ação |
|---------------|------|
| ⬆️ ⬇️ ⬅️ ➡️ | Mover o monstro / empurrar uma bola de neve |
| `L` | Mudar de nível |
| **Undo** / **Redo** | Anular / repetir jogadas |
| **Abrir Nível** | Carregar um nível a partir de um ficheiro de texto |

**Regras principais**

- Ao andar contra uma posição com neve, o monstro cria uma bola **pequena**.
- Empurrar uma bola por cima de neve faz com que **cresça** (pequena → média → grande).
- Os blocos (`BLOCK`) e os limites do tabuleiro impedem o movimento.
- Empilhamentos: média sobre grande (grande+média); pequena sobre média, sobre grande ou sobre grande+média. Pequena sobre grande+média completa o **boneco de neve**.
- O jogo tem 2 níveis. Ao completar o nível 1, passa automaticamente ao 2.
- Há pontuações (top scores) baseadas no número de movimentos, e música de fundo.

## Níveis personalizados

Podes carregar níveis com o botão **Abrir Nível**. Formato do ficheiro de texto:

```
<linhas> <colunas> <linha_monstro> <coluna_monstro>
<grelha linhas x colunas com: NO_SNOW | SNOW | BLOCK | SNOWMAN>
<bolas de neve iniciais>
```

Ver `LevelLoader.java` para os detalhes de leitura.

## Requisitos

- Java 17
- Maven (ou o wrapper incluído: `mvnw` / `mvnw.cmd`)
- JavaFX 19 (descarregado automaticamente pelo Maven)

## Executar

```bash
git clone https://github.com/sanina13/GoodSnowmanIsHardToBuild.git
cd GoodSnowmanIsHardToBuild
./mvnw compile
```

A aplicação arranca pela classe `pt.ipbeja.estig.po2.snowman.model.BoardModel` (tem o `main`). Abre o projeto no IntelliJ e corre essa classe.

## Estrutura

```
src/main/java/pt/ipbeja/estig/po2/snowman/
├── model/   # Lógica do jogo (BoardModel, Snowball, Monster, GameState, LevelLoader, Score, ...)
└── gui/     # Interface JavaFX (GameView)
src/main/resources/
├── images/  # Sprites
└── sound/   # Música de fundo
src/test/    # Testes (JUnit 5)
```

Destaques de design: separação modelo/vista, `GameState` para Undo/Redo (cópias profundas do estado), `Score` ordenável por número de movimentos.

## Autores

- Miguel Sanina (n.º 26874)
- Tiago Sanina (n.º 20318), [@sanina13](https://github.com/sanina13)

Parte do código foi desenvolvida com apoio de LLMs; os pontos estão assinalados no código com o comentário `HELP WITH LLMS`.
