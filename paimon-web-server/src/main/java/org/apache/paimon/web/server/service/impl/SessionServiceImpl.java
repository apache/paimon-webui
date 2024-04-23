package org.apache.paimon.web.server.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.paimon.web.engine.flink.sql.gateway.client.SqlGatewayClient;
import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;
import org.apache.paimon.web.server.data.dto.SessionDTO;
import org.apache.paimon.web.server.data.vo.UserVO;
import org.apache.paimon.web.server.service.SessionService;
import org.apache.paimon.web.server.service.UserService;
import org.apache.paimon.web.server.service.UserSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/** SessionServiceImpl. */
@Service
@Slf4j
public class SessionServiceImpl implements SessionService {

    private static final Integer ACTIVE_STATUS = 1;
    private static final Integer INACTIVE_STATUS = 0;

    @Autowired
    private UserSessionManager sessionManager;

    @Autowired
    private UserService userService;

    @Override
    public void createSession(SessionDTO sessionDTO) {
        try {
            SqlGatewayClient client =
                    new SqlGatewayClient(sessionDTO.getHost(), sessionDTO.getPort());
            if (getCurrentUser() != null) {
                String sessionName = getCurrentUser().getUsername() + "_" + UUID.randomUUID();
                SessionEntity sessionEntity = client.openSession(sessionName);
                sessionManager.addSession(getCurrentUser().getUsername(), sessionEntity);
            }
        } catch (Exception e) {
            log.error("Failed to create session", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeSession(SessionDTO sessionDTO) {
        try {
            SqlGatewayClient client =
                    new SqlGatewayClient(sessionDTO.getHost(), sessionDTO.getPort());
            if (getCurrentUser() != null) {
                SessionEntity session = sessionManager.getSession(getCurrentUser().getUsername());
                if (session != null) {
                    client.closeSession(session.getSessionId());
                }
            }
        } catch (Exception e) {
            log.error("Failed to close session", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int triggerSessionHeartbeat(SessionDTO sessionDTO) {
        try {
            if (getCurrentUser() != null) {
                SqlGatewayClient client =
                        new SqlGatewayClient(sessionDTO.getHost(), sessionDTO.getPort());
                SessionEntity session = sessionManager.getSession(getCurrentUser().getUsername());
                client.triggerSessionHeartbeat(session.getSessionId());
            }
        } catch (Exception e) {
            log.error("Unexpected error during session heartbeat", e);
           return INACTIVE_STATUS;
        }
        return ACTIVE_STATUS;
    }

    private UserVO getCurrentUser() {
        if (StpUtil.isLogin()) {
            return userService.getUserById(StpUtil.getLoginIdAsInt());
        }
        return null;
    }
}
