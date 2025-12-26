package application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Entity 标记该类为JPA实体类，表示这个类与数据库中的表进行映射
// Table 指定这个类对应数据库中哪个表格
// Id 标记这个字段为主键
// GeneratedValue 指定这个主键是数据库自增长的生成策略
// JsonIgnore 用于Jackson序列化和反序列化时忽略creation字段
@NoArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "empolyee_id")
    private Integer empolyee_id;
    @Column(name = "client_id")
    private Integer client_id;
    @Column(name = "storehouse_id")
    private Integer storehouse_id;
    @Column(name = "status")
    private String status;
    @Column(name = "client_name")
    private String user;



    public Point(int empolyee_id, int client_id, int storehouse_id, String status, String user) {
        this.empolyee_id = empolyee_id;
        this.client_id = client_id;
        this.storehouse_id = storehouse_id;
        this.status = status;
        this.user = user;
    }



}
