using Avalonia.Data;
using System.Collections.Generic;

namespace persistance
{
    /// <summary>
    /// Generic repository interface for CRUD operations.
    /// </summary>
    /// <typeparam name="TId">The type of the entity identifier.</typeparam>
    /// <typeparam name="TEntity">The type of the entity.</typeparam>
    public interface IRepository<TId, TEntity> where TEntity : class
    {
        /// <summary>
        /// Finds an entity by its identifier.
        /// </summary>
        /// <param name="id">The identifier of the entity to find.</param>
        /// <returns>An <see cref="Optional{TEntity}"/> containing the found entity, if any.</returns>
        Optional<TEntity> FindById(TId id);

        /// <summary>
        /// Finds all entities.
        /// </summary>
        /// <returns>An <see cref="IEnumerable{TEntity}"/> containing all entities.</returns>
        IEnumerable<TEntity> FindAll();

        /// <summary>
        /// Saves a new entity.
        /// </summary>
        /// <param name="entity">The entity to save.</param>
        /// <returns>An <see cref="Optional{TEntity}"/> containing the saved entity.</returns>
        Optional<TEntity> Save(TEntity entity);

        /// <summary>
        /// Updates an existing entity.
        /// </summary>
        /// <param name="entity">The entity to update.</param>
        /// <returns>An <see cref="Optional{TEntity}"/> containing the updated entity.</returns>
        Optional<TEntity> Update(TEntity entity);

        /// <summary>
        /// Deletes an entity by its identifier.
        /// </summary>
        /// <param name="id">The identifier of the entity to delete.</param>
        /// <returns>An <see cref="Optional{TEntity}"/> containing the deleted entity, if any.</returns>
        Optional<TEntity> Delete(TId id);
    }
}