package br.rafaeros.aura.modules.jiga.service;

import br.rafaeros.aura.modules.jiga.controller.dto.JigaProvisioningDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JigaProvisioningService {

    private final DynamoDbClient dynamoDbClient;
    @Value("${aws.dynamodb.table-name:AuraIoT_SingleTable}")
    private String tableName;

    public void processJigaData(JigaProvisioningDTO dto, Long companyId) {
        Map<String, AttributeValue> itemValues = new HashMap<>();
        String partitionKey = "TENANT#" + companyId;
        String sortKey = "DEVICE#" + dto.getDeviceId();
        itemValues.put("PK", AttributeValue.builder().s(partitionKey).build());
        itemValues.put("SK", AttributeValue.builder().s(sortKey).build());
        itemValues.put("DeviceId", AttributeValue.builder().s(dto.getDeviceId()).build());
        itemValues.put("ProductName", AttributeValue.builder().s(dto.getProductName()).build());
        itemValues.put("ProductVersion", AttributeValue.builder().s(dto.getProductVersion()).build());
        itemValues.put("TipoEntidade", AttributeValue.builder().s("JigaProvisioning").build());
        itemValues.put("Timestamp", AttributeValue.builder().n(String.valueOf(System.currentTimeMillis())).build());

        if (dto.getDynamicAttributes() != null) {
            dto.getDynamicAttributes().forEach((key, value) -> {
                itemValues.put(key, mapToAttributeValue(value));
            });
        }

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            dynamoDbClient.putItem(request);
            log.info("Dispositivo {} salvo/atualizado com sucesso para o Tenant {}", dto.getDeviceId(), companyId);
        } catch (Exception e) {
            log.error("Erro ao salvar dados no DynamoDB: {}", e.getMessage());
            throw new RuntimeException("Falha na integração com AWS DynamoDB", e);
        }
    }

    private AttributeValue mapToAttributeValue(Object value) {
        if (value == null) return AttributeValue.builder().nul(true).build();
        if (value instanceof String) return AttributeValue.builder().s((String) value).build();
        if (value instanceof Number) return AttributeValue.builder().n(String.valueOf(value)).build();
        if (value instanceof Boolean) return AttributeValue.builder().bool((Boolean) value).build();
        return AttributeValue.builder().s(value.toString()).build();
    }
}