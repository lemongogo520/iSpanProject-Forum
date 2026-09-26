package forum.board.forumtemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiForumTemplateService {

  private final Client geminiClient;
  private final ObjectMapper objectMapper;

  @Value("${GEMINI_MODEL:gemini-3.8-flash}")
  private String model;

  public ForumTemplateDto generate(AiTemplateRequest request) {

    String prompt = """
        你是一個論壇架構設計助手。

        請根據以下需求建立論壇分類及討論版。

        論壇需求：
        %s

        分類數量：%d
        每個分類的討論版數量：%d

        請只回傳合法 JSON，不要使用 Markdown，不要加 ```json。

        JSON 格式必須完全符合：
        {
          "code": "AI_GENERATED",
          "name": "AI 自訂論壇",
          "description": "論壇說明",
          "categories": [
            {
              "name": "分類名稱",
              "rank": 1,
              "boards": [
                {
                  "name": "討論版名稱",
                  "description": "討論版說明",
                  "rank": 1
                }
              ]
            }
          ]
        }

        規則：
        1. 必須產生剛好 %d 個分類。
        2. 每個分類必須產生剛好 %d 個討論版。
        3. rank 必須從 1 開始依序排列。
        4. 分類及討論版名稱使用繁體中文。
        5. code 必須固定為 AI_GENERATED。
        6. 不可以回傳 JSON 以外的文字。
        """.formatted(
        request.description(),
        request.categoryCount(),
        request.boardsPerCategory(),
        request.categoryCount(),
        request.boardsPerCategory());

    try {
      GenerateContentConfig config = GenerateContentConfig.builder()
          .responseMimeType("application/json")
          .build();

      GenerateContentResponse response = geminiClient.models.generateContent(
          model,
          prompt,
          config);

      String json = response.text();

      if (json == null || json.isBlank()) {
        throw new IllegalStateException("Gemini 沒有回傳論壇範本");
      }
      return objectMapper.readValue(json, ForumTemplateDto.class);

    } catch (Exception e) {
      throw new IllegalStateException(
          "AI 論壇範本產生失敗：" + e.getMessage(),
          e);
    }
  }
}