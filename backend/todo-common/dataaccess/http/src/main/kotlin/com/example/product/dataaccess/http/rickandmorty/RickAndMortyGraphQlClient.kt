package com.example.product.dataaccess.http.rickandmorty

import com.example.rickmorty.generated.client.CharacterGraphQLQuery
import com.example.rickmorty.generated.client.CharacterProjectionRoot
import com.example.rickmorty.generated.client.EpisodeGraphQLQuery
import com.example.rickmorty.generated.client.EpisodeProjectionRoot
import com.example.rickmorty.generated.client.LocationGraphQLQuery
import com.example.rickmorty.generated.client.LocationProjectionRoot
import com.example.rickmorty.generated.types.Character
import com.example.rickmorty.generated.types.Episode
import com.example.rickmorty.generated.types.Location
import com.netflix.graphql.dgs.client.MonoGraphQLClient
import com.netflix.graphql.dgs.client.codegen.GraphQLMultiQueryRequest
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import kotlinx.coroutines.reactor.awaitSingle

class RickAndMortyGraphQlClient(
    private val client: MonoGraphQLClient,
) {
    suspend fun findCharacterById(id: String): Character {
        val query = CharacterGraphQLQuery
            .newRequest()
            .id(id)
            .build()

        val projection = CharacterProjectionRoot<Nothing, Nothing>()
            .id()
            .name()
            .apply { episode().name().id() }

        val graphQlQuery = GraphQLQueryRequest(query, projection)
        val response = client.reactiveExecuteQuery(graphQlQuery.serialize()).awaitSingle()
        return response.extractValueAsObject(query.getOperationName(), Character::class.java)
    }

    suspend fun findCharacterEpisodeAndLocations(
        characterId: String,
        episodeId: String,
        locationIds: List<String>,
    ): CharacterEpisodeLocationsResponse {
        val characterQuery = CharacterGraphQLQuery
            .newRequest()
            .id(characterId)
            .build()
        val characterProjection = CharacterProjectionRoot<Nothing, Nothing>()
            .id()
            .name()
            .status()
            .species()
            .type()
            .gender()
            .image()
            .created()

        val episodeQuery = EpisodeGraphQLQuery
            .newRequest()
            .id(episodeId)
            .build()
        val episodeProjection = EpisodeProjectionRoot<Nothing, Nothing>()
            .id()
            .name()
            .air_date()
            .episode()
            .created()

        val locationQueries = locationIds.mapIndexed { index, id ->
            val query = LocationGraphQLQuery
                .newRequest()
                .id(id)
                .build()
                .apply { queryAlias = "location$index" }
            val projection = LocationProjectionRoot<Nothing, Nothing>()
                .id()
                .name()
                .type()
                .dimension()
                .created()
            GraphQLQueryRequest(query, projection)
        }

        val combinedQuery = GraphQLMultiQueryRequest(
            buildList {
                add(GraphQLQueryRequest(characterQuery, characterProjection))
                add(GraphQLQueryRequest(episodeQuery, episodeProjection))
                addAll(locationQueries)
            },
        )

        val response = client.reactiveExecuteQuery(combinedQuery.serialize()).awaitSingle()

        val character = response.extractValueAsObject(
            characterQuery.getOperationName(),
            Character::class.java,
        )

        val episode = response.extractValueAsObject(
            episodeQuery.getOperationName(),
            Episode::class.java,
        )

        val locations = locationIds.indices.map { index ->
            response.extractValueAsObject("location$index", Location::class.java)
        }

        return CharacterEpisodeLocationsResponse(
            character = character,
            episode = episode,
            locations = locations,
        )
    }
}

data class CharacterEpisodeLocationsResponse(
    val character: Character,
    val episode: Episode,
    val locations: List<Location>,
)
