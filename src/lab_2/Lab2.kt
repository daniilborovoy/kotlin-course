import java.time.LocalDate

// Базовый класс Person
open class Person(
    val name: String,
    val surname: String,
    val birthDate: LocalDate
) {
    open fun displayInfo() {
        println("Name: $name $surname, Birth Date: $birthDate")
    }
}

// Класс Student
class Student(
    name: String,
    surname: String,
    birthDate: LocalDate,
    val studentId: String
) : Person(name, surname, birthDate) {
    val coursesEnrolled = mutableListOf<Course>()

    fun enroll(course: Course) {
        coursesEnrolled.add(course)
        course.studentsEnrolled.add(this)
    }

    override fun displayInfo() {
        super.displayInfo()
        println("Student ID: $studentId, Enrolled Courses: ${coursesEnrolled.joinToString { it.courseName }}")
    }
}

// Класс Staff
open class Staff(
    name: String,
    surname: String,
    birthDate: LocalDate,
    val employeeId: String,
    val department: String
) : Person(name, surname, birthDate) {
    override fun displayInfo() {
        super.displayInfo()
        println("Employee ID: $employeeId, Department: $department")
    }
}

// Класс Teacher
class Teacher(
    name: String,
    surname: String,
    birthDate: LocalDate,
    employeeId: String,
    department: String,
    val subjectsTaught: List<String>
) : Staff(name, surname, birthDate, employeeId, department) {
    fun assignGrade(student: Student, course: Course, grade: String) {
        println("Assigned grade $grade to ${student.name} ${student.surname} for ${course.courseName}.")
    }
}

// Класс Administrator
class Administrator(
    name: String,
    surname: String,
    birthDate: LocalDate,
    employeeId: String,
    department: String,
    val responsibilities: List<String>
) : Staff(name, surname, birthDate, employeeId, department) {
    // Дополнительные методы для администратора могут быть добавлены здесь
}

// Класс Course
class Course(
    val courseId: String,
    val courseName: String,
    var teacher: Teacher? = null,
    val studentsEnrolled: MutableList<Student> = mutableListOf()
) {
    fun displayInfo() {
        println("Course ID: $courseId, Course Name: $courseName, Teacher: ${teacher?.name ?: "Not Assigned"}")
        println("Enrolled Students: ${studentsEnrolled.joinToString { "${it.name} ${it.surname}" }}")
    }
}

// Класс University
class University {
    private val people = mutableListOf<Person>()
    private val courses = mutableListOf<Course>()

    fun addStudent(name: String, surname: String, birthDate: LocalDate, studentId: String) {
        val student = Student(name, surname, birthDate, studentId)
        people.add(student)
        println("Added student: $name $surname")
    }

    fun addStaff(name: String, surname: String, birthDate: LocalDate, employeeId: String, department: String, isTeacher: Boolean = false, subjectsTaught: List<String> = emptyList()): Staff {
        val staff = if (isTeacher) {
            Teacher(name, surname, birthDate, employeeId, department, subjectsTaught)
        } else {
            Administrator(name, surname, birthDate, employeeId, department, emptyList())
        }
        people.add(staff)
        println("Added staff: $name $surname")
        return staff
    }

    fun createCourse(courseId: String, courseName: String, teacher: Teacher) {
        val course = Course(courseId, courseName, teacher)
        courses.add(course)
        teacher.subjectsTaught.forEach {
            println("Assigned teacher ${teacher.name} ${teacher.surname} to course $courseName")
        }
    }

    fun enrollStudentInCourse(studentId: String, courseId: String) {
        val student = people.filterIsInstance<Student>().find { it.studentId == studentId }
        val course = courses.find { it.courseId == courseId }

        if (student != null && course != null) {
            student.enroll(course)
            println("Enrolled ${student.name} ${student.surname} in course ${course.courseName}")
        } else {
            println("Student or Course not found.")
        }
    }

    fun displaySchedule() {
        courses.forEach { it.displayInfo() }
    }

    fun displayPeople() {
        people.forEach { it.displayInfo() }
    }
}

// Консольное приложение
fun main() {
    val university = University()

    // Пример добавления студентов и сотрудников
    university.addStudent("John", "Doe", LocalDate.of(2000, 1, 1), "S001")
    val teacher = university.addStaff("Alice", "Smith", LocalDate.of(1985, 5, 20), "T001", "Computer Science", true, listOf("Programming", "Algorithms"))
    university.addStaff("Bob", "Johnson", LocalDate.of(1990, 3, 15), "A001", "Administration")

    // Создание курсов и назначение преподавателей
    university.createCourse("C001", "Introduction to Programming", teacher as Teacher)

    // Запись студентов на курсы
    university.enrollStudentInCourse("S001", "C001")

    // Отображение расписания и информации о пользователях
    println("\n--- Schedule ---")
    university.displaySchedule()
    println("\n--- People ---")
    university.displayPeople()
}
