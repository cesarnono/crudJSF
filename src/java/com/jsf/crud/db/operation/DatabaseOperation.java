/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsf.crud.db.operation;

import com.jsf.crud.StudentBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.context.FacesContext;

/**
 *
 * @author HOGAR
 */
public class DatabaseOperation {

    public static Statement statement;
    public static Connection conn;
    public static ResultSet rs;
    public static PreparedStatement ps;

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String urlBD = "jdbc:mysql://localhost:3306/students";
            String userBD = "root";
            String passwUserBD = "admin123";
            conn = DriverManager.getConnection(urlBD, userBD, passwUserBD);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static ArrayList getStudentsListFromDB() {
        ArrayList studentList = new ArrayList();
        try {
            statement = getConnection().createStatement();
            rs = statement.executeQuery("select * from student_record");
            while (rs.next()) {
                StudentBean stb = new StudentBean();
                stb.setId(rs.getInt("student_id"));
                stb.setName(rs.getString("student_name"));
                stb.setEmail(rs.getString("student_email"));
                stb.setPassword(rs.getString("student_password"));
                stb.setGender(rs.getString("student_gender"));
                stb.setAddress(rs.getString("student_address"));
                studentList.add(stb);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("getStudentsListFromDB - finnaly ->" + e.getMessage());
                e.printStackTrace();
            }
        }
        return studentList;
    }

    public static String saveStudentDetailsInDB(StudentBean stb) {
        int saveResult = 0;
        String navigationResult = "";
        try {
            ps = getConnection().prepareStatement("insert into student_record (student_name, student_email, student_password, student_gender, student_address) values (?, ?, ?, ?, ?)");
            ps.setString(1, stb.getName());
            ps.setString(2, stb.getEmail());
            ps.setString(3, stb.getPassword());
            ps.setString(4, stb.getGender());
            ps.setString(5, stb.getAddress());
            saveResult = ps.executeUpdate();
            navigationResult = (saveResult != 0) ? "studentsList.xhtml?faces-redirect=true" : "createStudent.xhtml?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("saveStudentDetailsInDB - finnaly ->" + e.getMessage());
                e.printStackTrace();
            }
        }
        return navigationResult;
    }

    public static String editStudentRecordInDB(int studentId) {
        StudentBean editRecord = null;
        System.out.println("editStudentRecordInDB() : Student Id: " + studentId);

        /* Setting The Particular Student Details In Session */
        Map<String, Object> sessionMapObj = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        try {
            statement = getConnection().createStatement();
            rs = statement.executeQuery("select * from student_record where student_id = " + studentId);
            if (rs != null) {
                rs.next();
                editRecord = new StudentBean();
                editRecord.setId(rs.getInt("student_id"));
                editRecord.setName(rs.getString("student_name"));
                editRecord.setEmail(rs.getString("student_email"));
                editRecord.setGender(rs.getString("student_gender"));
                editRecord.setAddress(rs.getString("student_address"));
                editRecord.setPassword(rs.getString("student_password"));
            }
            System.out.println("editRecord.getPassword :"+editRecord.getPassword());
            sessionMapObj.put("editRecordObj", editRecord);

        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("editStudentRecordInDB - finnaly ->" + e.getMessage());
                e.printStackTrace();
            }
        }
        return "/editStudent.xhtml?faces-redirect=true";
    }

    /* Method Used To Update Student Record In Database */
    public static String updateStudentDetailsInDB(StudentBean updateStudentObj) {
        try {
            ps = getConnection().prepareStatement("update student_record set student_name=?, student_email=?, student_password=?, student_gender=?, student_address=? where student_id=?");
            ps.setString(1, updateStudentObj.getName());
            ps.setString(2, updateStudentObj.getEmail());
            ps.setString(3, updateStudentObj.getPassword());
            ps.setString(4, updateStudentObj.getGender());
            ps.setString(5, updateStudentObj.getAddress());
            ps.setInt(6, updateStudentObj.getId());
            ps.executeUpdate();
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("updateStudentDetailsInDB - finnaly ->" + e.getMessage());
                e.printStackTrace();
            }
        }
        return "/studentsList.xhtml?faces-redirect=true";
    }

    /* Method Used To Delete Student Record From Database */
    public static String deleteStudentRecordInDB(int studentId) {
        System.out.println("deleteStudentRecordInDB() : Student Id: " + studentId);
        try {
            ps = getConnection().prepareStatement("delete from student_record where student_id = " + studentId);
            ps.executeUpdate();            
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("deleteStudentRecordInDB - finnaly ->" + e.getMessage());
                e.printStackTrace();
            }
        }
        return "/studentsList.xhtml?faces-redirect=true";
    }

}
