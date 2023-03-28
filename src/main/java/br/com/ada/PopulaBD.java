package br.com.ada;

import com.github.javafaker.Faker;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class PopulaBD {

    private static String DATA_BASE_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static String DATA_BASE_USER = "admin";
    private static String DATA_BASE_PASSWORD = "admin";

    public static void main(String[] args){
        try {
            atribuirEndereco();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void criarAluno(Integer qtdAlunos) throws SQLException {
        for (int i = 0; i < qtdAlunos; i++){
            Faker faker = new Faker();
            String nome = faker.name().fullName();
            Date dtNascimento =  faker.date().birthday();
            String cpf = faker.number().digits(11);
            String sexo = getSexo(faker);
            String email =nome.toLowerCase()
                    .replaceAll("\\s", "").concat("@gmail.com");
            String telefone = faker.number().digits(11);
            Connection con = DriverManager.getConnection(
                    DATA_BASE_URL,
                    DATA_BASE_USER,
                    DATA_BASE_PASSWORD
            );

            PreparedStatement ps = con.prepareStatement(
                    "insert into public.aluno " +
                            "(nome, dt_nascimento, cpf, sexo, email, telefone) " +
                            "values(?, ?, ?, ?, ?, ?)");
            ps.setString(1, nome);
            ps.setDate(2, new java.sql.Date(dtNascimento.getTime()));
            ps.setString(3, cpf);
            ps.setString(4, sexo);
            ps.setString(5, email);
            ps.setString(6, telefone);

            int row = ps.executeUpdate();
            System.out.println(row);
        }
    }

    public static void atribuirEndereco() throws SQLException {
        Connection con = DriverManager.getConnection(
                DATA_BASE_URL,
                DATA_BASE_USER,
                DATA_BASE_PASSWORD
        );
        PreparedStatement ps = con.prepareStatement("select a.id from aluno a full join endereco e on e.id_aluno = a.id where e.id is null");
        ResultSet rs = ps.executeQuery();
        Faker faker = new Faker();

        while (rs.next()){
            ps = con.prepareStatement("INSERT INTO public.endereco (id_aluno, logradouro, nome, numero, complemento, cep) VALUES(?, ?, ?, ?, ?, ?)");
            ps.setInt(1, rs.getInt("id"));
            ps.setString(2, faker.address().streetAddress());
            ps.setString(3, "residencial");
            ps.setString(4, String.valueOf(faker.number().numberBetween(0, 10000)));
            ps.setString(5, "ap " + faker.number().numberBetween(10, 1000));
            ps.setString(6, faker.number().digits(8));
            ps.executeUpdate();
        }

    }

    private static String getSexo(Faker faker) {
        return faker.number().randomDigit() > 4 ? "F" : "M";
    }

}
