package com.freshdelivery.mapper.report;

import com.freshdelivery.entity.report.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReportMapper {

    @Select("SELECT a.delivery_date, c.id AS customer_id, c.name AS customer_name, " +
            "COUNT(*) AS order_count, COALESCE(SUM(a.total_amount), 0) AS total_amount " +
            "FROM acceptance a LEFT JOIN customer c ON a.customer_id = c.id " +
            "WHERE a.delivery_date BETWEEN #{start} AND #{end} " +
            "GROUP BY a.delivery_date, c.id, c.name " +
            "ORDER BY a.delivery_date DESC, c.id")
    List<SalesDailyRow> selectSalesDaily(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Select("SELECT DATE_FORMAT(a.delivery_date, '%Y-%m') AS month, " +
            "c.id AS customer_id, c.name AS customer_name, " +
            "COUNT(*) AS order_count, COALESCE(SUM(a.total_amount), 0) AS total_amount, " +
            "COALESCE(SUM(a.total_loss_amount), 0) AS loss_amount " +
            "FROM acceptance a LEFT JOIN customer c ON a.customer_id = c.id " +
            "WHERE a.delivery_date BETWEEN #{start} AND #{end} " +
            "GROUP BY DATE_FORMAT(a.delivery_date, '%Y-%m'), c.id, c.name " +
            "ORDER BY month DESC, c.id")
    List<MonthlyRow> selectMonthly(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Select("SELECT DATE_FORMAT(a.delivery_date, '%Y-%m') AS month, " +
            "COALESCE(SUM(a.total_amount), 0) AS total_amount " +
            "FROM acceptance a " +
            "WHERE a.delivery_date BETWEEN #{start} AND #{end} " +
            "GROUP BY DATE_FORMAT(a.delivery_date, '%Y-%m') " +
            "ORDER BY month")
    List<MonthlyAmount> selectSalesByMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Select("SELECT DATE_FORMAT(po.order_date, '%Y-%m') AS month, " +
            "COALESCE(SUM(po.total_amount), 0) AS total_amount " +
            "FROM purchase_order po " +
            "WHERE po.order_date BETWEEN #{start} AND #{end} " +
            "GROUP BY DATE_FORMAT(po.order_date, '%Y-%m') " +
            "ORDER BY month")
    List<MonthlyAmount> selectPurchaseByMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Select("SELECT o.order_date, c.id AS customer_id, c.name AS customer_name, " +
            "oi.item_name, oi.item_spec, oi.item_unit, oi.quantity, oi.unit_price, oi.subtotal " +
            "FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "LEFT JOIN customer c ON o.customer_id = c.id " +
            "WHERE o.order_date BETWEEN #{start} AND #{end} " +
            "AND (#{customerId} IS NULL OR o.customer_id = #{customerId}) " +
            "ORDER BY o.order_date DESC, o.customer_id")
    List<SalesDetailRow> selectSalesDetail(@Param("start") LocalDate start, @Param("end") LocalDate end,
                                           @Param("customerId") Long customerId);

    @Select("SELECT a.delivery_date, c.id AS customer_id, c.name AS customer_name, " +
            "ai.item_name, ai.item_spec, ai.delivered_quantity, ai.actual_quantity, " +
            "ai.loss_quantity, ai.unit_price " +
            "FROM acceptance_item ai " +
            "JOIN acceptance a ON ai.acceptance_id = a.id " +
            "LEFT JOIN customer c ON a.customer_id = c.id " +
            "WHERE a.delivery_date BETWEEN #{start} AND #{end} " +
            "AND (#{customerId} IS NULL OR a.customer_id = #{customerId}) " +
            "ORDER BY a.delivery_date DESC, a.customer_id")
    List<LossRow> selectLossReport(@Param("start") LocalDate start, @Param("end") LocalDate end,
                                   @Param("customerId") Long customerId);

    @Select("SELECT po.order_date, s.id AS supplier_id, s.name AS supplier_name, " +
            "pi.item_name, pi.item_spec, pi.quantity, pi.unit_price, pi.subtotal " +
            "FROM purchase_item pi " +
            "JOIN purchase_order po ON pi.order_id = po.id " +
            "LEFT JOIN supplier s ON po.supplier_id = s.id " +
            "WHERE po.order_date BETWEEN #{start} AND #{end} " +
            "AND (#{supplierId} IS NULL OR po.supplier_id = #{supplierId}) " +
            "ORDER BY po.order_date DESC, po.supplier_id")
    List<PurchaseRow> selectPurchaseReport(@Param("start") LocalDate start, @Param("end") LocalDate end,
                                           @Param("supplierId") Long supplierId);

    @Select("SELECT DATE_FORMAT(a.delivery_date, '%Y-%m') AS month, " +
            "c.id AS customer_id, c.name AS customer_name, " +
            "COUNT(*) AS order_count, COALESCE(SUM(a.total_amount), 0) AS total_amount, " +
            "COALESCE(SUM(a.total_loss_amount), 0) AS loss_amount, " +
            "c.settlement_cycle AS settlement_cycle " +
            "FROM acceptance a LEFT JOIN customer c ON a.customer_id = c.id " +
            "WHERE a.delivery_date BETWEEN #{start} AND #{end} " +
            "AND (#{customerId} IS NULL OR a.customer_id = #{customerId}) " +
            "GROUP BY DATE_FORMAT(a.delivery_date, '%Y-%m'), c.id, c.name, c.settlement_cycle " +
            "ORDER BY month DESC, c.id")
    List<StatementRow> selectStatement(@Param("start") LocalDate start, @Param("end") LocalDate end,
                                       @Param("customerId") Long customerId);
}