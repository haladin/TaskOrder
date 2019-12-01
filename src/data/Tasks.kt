package com.example.data

import com.fasterxml.jackson.annotation.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("tasks")
class Tasks {
    @get:JsonProperty("tasks")
    @set:JsonProperty("tasks")
    @JsonProperty("tasks")
    var tasks: List<Task>? = null
    @JsonIgnore
    private val additionalProperties: MutableMap<String, Any> =
        HashMap()

    @JsonAnyGetter
    fun getAdditionalProperties(): Map<String, Any> {
        return additionalProperties
    }

    @JsonAnySetter
    fun setAdditionalProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }

    companion object {
        fun getOrderedTasks(tasks : List<Task>): Response<List<Task>> {
            val mapOfTasks = tasks.map { it to (it.requires?.size ?: 0 )}.toMap().toMutableMap()

            val queue = mapOfTasks.filterValues { it == 0 }.keys.toMutableList()
            val orderedList = mutableListOf<Task>()
            var counter = 0
            while (queue.isNotEmpty()) {
                val task = queue.removeAt(0)
                orderedList.add(task)
                for (t in mapOfTasks.keys){
                    t.requires?.let {
                        if (it.contains(task.name)) {
                            mapOfTasks[t] = mapOfTasks[t]!! - 1
                            if (mapOfTasks[t] == 0) {
                                queue.add(t)
                            }
                        }
                    }
                }
                counter++
            }

            return if (counter != tasks.size) {
                Response.Error("There is an error in the graph. Loop or missing node.")
            } else {
                Response.Success(orderedList)
            }
        }
    }
}