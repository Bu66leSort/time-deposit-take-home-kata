package org.ikigaidigital.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetTimeDepositsIntegrationTest extends IntegrationTestBase {

    @Test
    void returnsEmptyArrayWhenNoDeposits() throws Exception {
        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Sql("/dbscripts/init_deposits.sql")
    void returnsDepositsOrderedByIdWithDaysAndWithdrawals() throws Exception {
        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].planType").value("basic"))
                .andExpect(jsonPath("$[0].balance").value(12000.0))
                .andExpect(jsonPath("$[0].days").value(45))
                .andExpect(jsonPath("$[0].withdrawals.length()").value(2))
                .andExpect(jsonPath("$[0].withdrawals[0].timeDepositId").value(1))
                .andExpect(jsonPath("$[1].withdrawals.length()").value(0))
                .andExpect(jsonPath("$[3].days").value(366))
                .andExpect(jsonPath("$[4].withdrawals.length()").value(1));
    }

    @Test
    @Sql("/dbscripts/init_deposits.sql")
    void daysFollowTheClock() throws Exception {
        clock.setDate(TODAY.plusDays(10));

        mockMvc.perform(get("/time-deposits"))
                .andExpect(jsonPath("$[0].days").value(55));
    }

    @Test
    @Sql("/dbscripts/init_deposits.sql")
    void paginatesByPageAndSize() throws Exception {
        mockMvc.perform(get("/time-deposits").param("page", "1").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(4));

        mockMvc.perform(get("/time-deposits").param("page", "5").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Sql("/dbscripts/init_many_deposits.sql")
    void capsPageSizeAt100() throws Exception {
        mockMvc.perform(get("/time-deposits").param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100));
    }

    @Test
    @Sql("/dbscripts/init_many_deposits.sql")
    void defaultPageSizeIs20() throws Exception {
        mockMvc.perform(get("/time-deposits"))
                .andExpect(jsonPath("$.length()").value(20));
    }
}
