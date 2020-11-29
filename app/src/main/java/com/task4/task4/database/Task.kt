package com.task4.task4.database

import androidx.room.*
import java.text.DateFormat
import java.util.Date
import java.util.UUID

@Entity
data class Task(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var completed: Boolean = false,
    var dueDate: Date = Date(),
) {

    val userDate: String
        get() = DateFormat.getDateInstance(DateFormat.MEDIUM).format(dueDate)

    val userTime: String
        get() = DateFormat.getTimeInstance(DateFormat.SHORT).format(dueDate)
}

@Entity(
    indices = [Index("parentId"), Index("childId")],
    primaryKeys = ["parentId", "childId"],
    foreignKeys = [ForeignKey(
        entity = Task::class, parentColumns = ["id"], childColumns = ["parentId"]
    ), ForeignKey(
        entity = Task::class, parentColumns = ["id"], childColumns = ["childId"]
    )]
)
data class TaskCrossRef(
    val parentId: UUID,
    val childId: UUID,
)

data class TaskWithSubTasks(
    @Embedded val parent: Task, @Relation(
        parentColumn = "id", entityColumn = "id", associateBy = Junction(
            value = TaskCrossRef::class, parentColumn = "parentId", entityColumn = "childId"
        )
    ) val subTasks: List<Task>
) {

    val canBeCompleted: Boolean
        get() = subTasks.all { it.completed }
}