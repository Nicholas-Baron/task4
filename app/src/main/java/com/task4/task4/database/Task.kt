package com.task4.task4.database

import androidx.room.*
import java.util.Date
import java.util.UUID

@Entity
data class Task(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var completed: Boolean = false,
    var dueDate: Date = Date(),
)

@Entity(primaryKeys = ["parentId", "childId"])
data class TaskCrossRef(
    val parentId: UUID,
    val childId: UUID,
)

data class TaskWithSubTasks(
    @Embedded val parent: Task, @Relation(
        parentColumn = "parentId",
        entityColumn = "childId",
        associateBy = Junction(TaskCrossRef::class)
    ) val subTasks: List<Task>
)