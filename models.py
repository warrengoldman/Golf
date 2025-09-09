from typing import List

from sqlalchemy.orm import Mapped, relationship, mapped_column

from database import Base
from sqlalchemy import Column, Integer, String, Date, DateTime, Time, ForeignKey


class Event(Base):
    __tablename__ = 'event'

    id: Mapped[int] = mapped_column(primary_key=True)
    event_name = Column(String, unique=True, index=True)
    description = Column(String, nullable=True)
    create_date = Column(DateTime, nullable=True)
    event_dates: Mapped[List["EventDate"]] = relationship(back_populates="event", cascade="all, delete, delete-orphan")

class EventDate(Base):
    __tablename__ = 'event_date'

    id: Mapped[int] = mapped_column(primary_key=True)
    event_id: Mapped[int] = mapped_column(ForeignKey("event.id")) # Foreign key to Event
    event: Mapped["Event"] = relationship(back_populates="event_dates")
    event_date = Column(Date, nullable=False, index=True)
    event_active = Column(Integer, nullable=False, index=True)  # 0 = inactive, 1 = active
    create_date = Column(DateTime, nullable=True)
    activities: Mapped[List["Activity"]] = relationship(back_populates="event_date", cascade="all, delete, delete-orphan")

class Activity(Base):
    __tablename__ = 'activity'

    id: Mapped[int] = mapped_column(primary_key=True)
    event_date_id: Mapped[int] = mapped_column(ForeignKey("event_date.id")) # Foreign key to EventDate
    event_date: Mapped["EventDate"] = relationship(back_populates="activities")
    activity_name = Column(String, nullable=False)
    activity_time = Column(Time, nullable=False)
    create_date = Column(DateTime, nullable=True)
    participants: Mapped[List["Participant"]] = relationship(back_populates="activity", cascade="all, delete, delete-orphan")

class Participant(Base):
    __tablename__ = 'participant'

    id: Mapped[int] = mapped_column(primary_key=True)
    activity_id: Mapped[int] = mapped_column(ForeignKey("activity.id")) # Foreign key to Activity
    activity: Mapped["Activity"] = relationship(back_populates="participants")
    participant_name = Column(String, nullable=False)
    contact_info = Column(String, nullable=True)
    create_date = Column(DateTime, nullable=True)