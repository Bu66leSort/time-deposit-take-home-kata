package org.ikigaidigital.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateBalancesIntegrationTest extends IntegrationTestBase {

    @Autowired
    JdbcTemplate jdbc;

    private double balanceOf(int id) {
        return jdbc.queryForObject("SELECT balance FROM time_deposits WHERE id = ?", Double.class, id);
    }

    private int daysOf(int id) {
        return jdbc.queryForObject("SELECT days FROM time_deposits WHERE id = ?", Integer.class, id);
    }

    @Test
    void returnsZeroWhenNoDeposits() throws Exception {
        mockMvc.perform(patch("/time-deposits/balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedDeposits").value(0));
    }

    @Test
    @Sql("/dbscripts/init_deposits.sql")
    void accruesInterestPerPlanRules() throws Exception {
        // deposit 7 was already accrued for March, so 6 are processed.
        mockMvc.perform(patch("/time-deposits/balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedDeposits").value(6));

        assertThat(balanceOf(1)).isEqualTo(12010.0);  // basic, 45 days
        assertThat(balanceOf(2)).isEqualTo(12000.0);  // basic, 30 days: grace period
        assertThat(balanceOf(3)).isEqualTo(12030.0);  // student, 100 days
        assertThat(balanceOf(4)).isEqualTo(12000.0);  // student, 366 days: no interest after a year
        assertThat(balanceOf(5)).isEqualTo(12050.0);  // premium, 46 days
        assertThat(balanceOf(6)).isEqualTo(12000.0);  // unknown plan
        assertThat(balanceOf(7)).isEqualTo(12000.0);  // already accrued this month
    }

    @Test
    @Sql("/dbscripts/init_deposits.sql")
    void secondCallInTheSameMonthChangesNothing() throws Exception {
        mockMvc.perform(patch("/time-deposits/balances")).andExpect(status().isOk());

        mockMvc.perform(patch("/time-deposits/balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedDeposits").value(0));

        assertThat(balanceOf(1)).isEqualTo(12010.0);
        assertThat(balanceOf(5)).isEqualTo(12050.0);
    }

    @Test
    @Sql("/dbscripts/init_deposits.sql")
    void accruesAgainInTheNextMonth() throws Exception {
        mockMvc.perform(patch("/time-deposits/balances")).andExpect(status().isOk());

        clock.setDate(TODAY.plusMonths(1));
        mockMvc.perform(patch("/time-deposits/balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedDeposits").value(7));

        assertThat(balanceOf(1)).isEqualTo(12020.01);   // 12010 * 0.01 / 12 = 10.008 -> 10.01
        assertThat(balanceOf(2)).isEqualTo(12010.0);    // 61 days now: past the grace period
    }

    @Test
    @Sql("/dbscripts/init_deposits.sql")
    void storesRefreshedDays() throws Exception {
        clock.setDate(TODAY.plusDays(5));
        mockMvc.perform(patch("/time-deposits/balances")).andExpect(status().isOk());

        assertThat(daysOf(1)).isEqualTo(50);
    }

    @Test
    @Sql("/dbscripts/init_many_deposits.sql")
    void processesAllDepositsAcrossChunks() throws Exception {
        mockMvc.perform(patch("/time-deposits/balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedDeposits").value(1203));

        Integer notUpdated = jdbc.queryForObject(
                "SELECT count(*) FROM time_deposits WHERE balance <> 12010.00", Integer.class);
        assertThat(notUpdated).isZero();
    }

    @Test
    @Sql("/dbscripts/init_deposits.sql")
    void updatedBalanceIsVisibleInTheList() throws Exception {
        mockMvc.perform(patch("/time-deposits/balances")).andExpect(status().isOk());

        mockMvc.perform(get("/time-deposits"))
                .andExpect(jsonPath("$[0].balance").value(12010.0));
    }
}
