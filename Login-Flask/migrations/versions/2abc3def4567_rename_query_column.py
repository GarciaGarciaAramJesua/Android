"""Rename query to search_query in SearchHistory

Revision ID: 2abc3def4567
Revises: 1effd1850c91
Create Date: 2025-12-17 12:50:00.000000

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '2abc3def4567'
down_revision = '1effd1850c91'
branch_labels = None
depends_on = None


def upgrade():
    # Renombrar columna query a search_query
    with op.batch_alter_table('search_history', schema=None) as batch_op:
        batch_op.alter_column('query',
                              existing_type=sa.VARCHAR(length=200),
                              nullable=False,
                              new_column_name='search_query')


def downgrade():
    # Revertir el cambio
    with op.batch_alter_table('search_history', schema=None) as batch_op:
        batch_op.alter_column('search_query',
                              existing_type=sa.VARCHAR(length=200),
                              nullable=False,
                              new_column_name='query')
