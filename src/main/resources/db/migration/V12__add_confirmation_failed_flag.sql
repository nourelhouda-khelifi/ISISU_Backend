-- Add confirmation_failed flag to track failed confirmation questions
-- This column tracks when a confirmation question fails, indicating a confirmed gap

ALTER TABLE question_session 
ADD COLUMN confirmation_failed BOOLEAN NOT NULL DEFAULT false;

-- Comment explaining the column purpose
COMMENT ON COLUMN question_session.confirmation_failed IS 
'Flag to track if a confirmation question has failed. Used to detect confirmed gaps (2nd failure at same level).';
