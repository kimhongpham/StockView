//package com.recognition.service;
//
//import com.automarkettracker.backend.entity.Alert;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@Transactional(readOnly = true)
//public class AlertService {
//
//    // private final AlertRepository alertRepository;
//    // private final AlertLogRepository alertLogRepository;
//    // private final AssetRepository assetRepository;
//    // private final UserRepository userRepository;
//    // private final AlertMapper alertMapper;
//    // private final NotificationService notificationService;
//
//    // public Page<AlertDto> findUserAlerts(UUID userId, Pageable pageable) {
//    // log.debug("Finding alerts for user: {}", userId);
//    // Page<Alert> alerts = alertRepository.findByUserId(userId, pageable);
//    // return alerts.map(alertMapper::toDto);
//    // }
//
//    // @Transactional
//    // public AlertDto createAlert(UUID userId, CreateAlertRequest request) {
//    // log.info("Creating alert for user: {} on asset: {}", userId,
//    // request.getAssetId());
//
//    // Users user = userRepository.findById(userId)
//    // .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " +
//    // userId));
//
//    // Asset asset = assetRepository.findById(request.getAssetId())
//    // .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: "
//    // + request.getAssetId()));
//
//    // Alert alert = new Alert();
//    // alert.setUserId(user.getId());
//    // alert.setAssetId(asset.getId());
//    // alert.setAlertType(request.getAlertType());
//    // alert.setThresholdValue(request.getThresholdValue());
//    // alert.setConditionType(request.getConditionType());
//    // alert.setNotificationMethod(request.getNotificationMethod());
//    // alert.setNotificationTarget(request.getNotificationTarget());
//    // alert.setIsActive(true);
//    // alert.setCreatedAt(OffsetDateTime.now());
//    // alert.setUpdatedAt(OffsetDateTime.now());
//
//    // Alert savedAlert = alertRepository.save(alert);
//    // return alertMapper.toDto(savedAlert);
//    // }
//
//    // @Transactional
//    // public AlertDto updateAlert(UUID userId, UUID alertId, CreateAlertRequest
//    // request) {
//    // log.info("Updating alert: {} for user: {}", alertId, userId);
//
//    // Alert alert = alertRepository.findById(alertId)
//    // .filter(a -> a.getUserId().equals(userId))
//    // .orElseThrow(() -> new ResourceNotFoundException("Alert not found with ID: "
//    // + alertId));
//
//    // alert.setAlertType(request.getAlertType());
//    // alert.setThresholdValue(request.getThresholdValue());
//    // alert.setConditionType(request.getConditionType());
//    // alert.setNotificationMethod(request.getNotificationMethod());
//    // alert.setNotificationTarget(request.getNotificationTarget());
//    // alert.setUpdatedAt(OffsetDateTime.now());
//
//    // Alert updatedAlert = alertRepository.save(alert);
//    // return alertMapper.toDto(updatedAlert);
//    // }
//
//    // @Transactional
//    // public void deleteAlert(UUID userId, UUID alertId) {
//    // log.info("Deleting alert: {} for user: {}", alertId, userId);
//
//    // Alert alert = alertRepository.findById(alertId)
//    // .filter(a -> a.getUserId().equals(userId))
//    // .orElseThrow(() -> new ResourceNotFoundException("Alert not found with ID: "
//    // + alertId));
//
//    // alertRepository.delete(alert);
//    // }
//
//    @Async
//    @Transactional
//    public void checkAlertsForAsset(UUID assetId, BigDecimal currentPrice) {
//        log.debug("Checking alerts for asset: {} with price: {}", assetId, currentPrice);
//
////        List<Alert> activeAlerts = alertRepository.findByAssetIdAndIsActive(assetId, true);
////
////        for (Alert alert : activeAlerts) {
////            if (shouldTriggerAlert(alert, currentPrice)) {
////                triggerAlert(alert, currentPrice);
////            }
////        }
//    }
//
//    // private boolean shouldTriggerAlert(Alert alert, BigDecimal currentPrice) {
//    // BigDecimal threshold = alert.getThresholdValue();
//    // ConditionType condition = ConditionType.valueOf(alert.getConditionType());
//
//    // return switch (condition) {
//    // case GREATER_THAN -> currentPrice.compareTo(threshold) > 0;
//    // case LESS_THAN -> currentPrice.compareTo(threshold) < 0;
//    // case EQUAL_TO -> currentPrice.compareTo(threshold) == 0;
//    // };
//    // }
//
//    // @Transactional
//    // private void triggerAlert(Alert alert, BigDecimal triggeredPrice) {
//    // log.info("Triggering alert: {} for price: {}", alert.getId(),
//    // triggeredPrice);
//
//    // AlertLog alertLog = new AlertLog();
//    // alertLog.setAlertId(alert.getId());
//    // alertLog.setTriggeredPrice(triggeredPrice);
//    // alertLog.setTriggeredAt(OffsetDateTime.now());
//    // alertLog.setNotificationStatus(NotificationStatus.PENDING.name());
//    // alertLog.setRetryCount(0);
//
//    // alertLogRepository.save(alertLog);
//
//    // try {
//    // String message = buildAlertMessage(alert, triggeredPrice);
//    // notificationService.sendNotification(
//    // alert.getNotificationMethod(),
//    // alert.getNotificationTarget(),
//    // message);
//
//    // alertLog.setNotificationStatus(NotificationStatus.SENT.name());
//    // alert.setLastTriggered(OffsetDateTime.now());
//
//    // } catch (Exception e) {
//    // log.error("Failed to send notification for alert: {}", alert.getId(), e);
//    // alertLog.setNotificationStatus(NotificationStatus.FAILED.name());
//    // alertLog.setErrorMessage(e.getMessage());
//    // }
//
//    // alertLogRepository.save(alertLog);
//    // alertRepository.save(alert);
//    // }
//
//    // private String buildAlertMessage(Alert alert, BigDecimal triggeredPrice) {
//    // return String.format(
//    // "🚨 Price Alert: Asset %s has reached %s %.2f. Current price: %.2f",
//    // alert.getAssetId(),
//    // alert.getConditionType(),
//    // alert.getThresholdValue(),
//    // triggeredPrice);
//    // }
//}