// ================================================================
// UserProfilePage.tsx  —  /profile  (protected)
// Step 1 of 2 onboarding — basic profile setup.
// PUT /api/v1/users/me (UserController)
//
// UserProfileRequest fields (backend @Valid):
//   fullName     @NotBlank @Size(2–150)
//   phoneNumber  optional  regex ^[6-9]\d{9}$
//   city         @NotBlank max 100
//   state        @NotBlank max 100
//   age          @Min(18)  @Max(100)
// ================================================================

import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuthStore  from '../store/authStore';
import useUserStore  from '../store/userStore';
import { ROUTES, INDIAN_STATES } from '../utils/constants';
import { validateAge, validateFullName, validatePhone, validateRequired } from '../utils/scoreHelpers';
import type { UserProfileRequest } from '../types/user.types';
import './UserProfilePage.css';

const UserProfilePage = () => {
  const navigate   = useNavigate();
  const user       = useAuthStore(s => s.user);
  const { isUpdating, updateError, fieldErrors, updateProfile, clearErrors } = useUserStore();

  const [form, setForm] = useState<UserProfileRequest>({
    fullName:    user?.fullName    ?? '',
    phoneNumber: user?.phoneNumber ?? '',
    city:        user?.city        ?? '',
    state:       user?.state       ?? '',
    age:         user?.age         ?? ('' as unknown as number),
  });

  const [localErrors, setLocalErrors] = useState<Record<string, string>>({});

  // Merge backend 400 validation errors into local error map
  useEffect(() => {
    if (fieldErrors) setLocalErrors(prev => ({ ...prev, ...fieldErrors }));
  }, [fieldErrors]);

  const handleChange = (key: keyof UserProfileRequest, value: string | number) => {
    setForm(f => ({ ...f, [key]: value }));
    setLocalErrors(e => ({ ...e, [key]: '' }));
    clearErrors();
  };

  // Client-side validation mirrors backend @Valid annotations
  const validate = (): boolean => {
    const errs: Record<string, string> = {};
    const nameErr  = validateFullName(form.fullName);
    const ageErr   = validateAge(Number(form.age));
    const phoneErr = validatePhone(form.phoneNumber);
    const cityErr  = validateRequired(form.city, 'City');
    const stateErr = validateRequired(form.state, 'State');
    if (nameErr)  errs.fullName    = nameErr;
    if (ageErr)   errs.age         = ageErr;
    if (phoneErr) errs.phoneNumber = phoneErr;
    if (cityErr)  errs.city        = cityErr;
    if (stateErr) errs.state       = stateErr;
    setLocalErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    updateProfile(
      { ...form, age: Number(form.age) },
      () => navigate(ROUTES.FINANCIAL_PROFILE),
    );
  };

  const isFirstTime = !user?.isProfileComplete;

  return (
    <div className="user-profile-page">
      <div className="user-profile-page__container">

        <span className="user-profile-page__step-badge">
          {isFirstTime ? '✦ Step 1 of 2 — Basic Profile' : '✦ Edit Profile'}
        </span>

        <h1 className="user-profile-page__title">
          {isFirstTime ? 'Tell us about yourself' : 'Your Profile'}
        </h1>

        <p className="user-profile-page__sub">
          {isFirstTime
            ? 'Helps us personalise loan eligibility and card recommendations for you.'
            : 'Keep your details up to date for accurate recommendations.'}
        </p>

        <div className="user-profile-page__card">
          <form onSubmit={handleSubmit}>
            <div className="form-grid">

              {/* Full Name — full width */}
              <div className="form-group col-full">
                <label className="form-label">
                  Full Name <span className="required">*</span>
                </label>
                <input
                  className={`form-input${localErrors.fullName ? ' input-error' : ''}`}
                  placeholder="As on PAN card"
                  value={form.fullName}
                  onChange={e => handleChange('fullName', e.target.value)}
                />
                {localErrors.fullName && (
                  <span className="form-error">{localErrors.fullName}</span>
                )}
              </div>

              {/* Age */}
              <div className="form-group">
                <label className="form-label">
                  Age <span className="required">*</span>
                </label>
                <input
                  className={`form-input${localErrors.age ? ' input-error' : ''}`}
                  type="number"
                  min={18}
                  max={100}
                  placeholder="e.g. 28"
                  value={form.age}
                  onChange={e => handleChange('age', e.target.value)}
                />
                {localErrors.age && (
                  <span className="form-error">{localErrors.age}</span>
                )}
              </div>

              {/* Phone */}
              <div className="form-group">
                <label className="form-label">Mobile Number</label>
                <input
                  className={`form-input${localErrors.phoneNumber ? ' input-error' : ''}`}
                  type="tel"
                  placeholder="10-digit number"
                  maxLength={10}
                  value={form.phoneNumber}
                  onChange={e => handleChange('phoneNumber', e.target.value)}
                />
                {localErrors.phoneNumber && (
                  <span className="form-error">{localErrors.phoneNumber}</span>
                )}
              </div>

              {/* City */}
              <div className="form-group">
                <label className="form-label">
                  City <span className="required">*</span>
                </label>
                <input
                  className={`form-input${localErrors.city ? ' input-error' : ''}`}
                  placeholder="e.g. Chennai"
                  value={form.city}
                  onChange={e => handleChange('city', e.target.value)}
                />
                {localErrors.city && (
                  <span className="form-error">{localErrors.city}</span>
                )}
              </div>

              {/* State */}
              <div className="form-group">
                <label className="form-label">
                  State <span className="required">*</span>
                </label>
                <select
                  className={`form-input${localErrors.state ? ' input-error' : ''}`}
                  value={form.state}
                  onChange={e => handleChange('state', e.target.value)}
                >
                  <option value="">Select state</option>
                  {INDIAN_STATES.map(s => (
                    <option key={s} value={s}>{s}</option>
                  ))}
                </select>
                {localErrors.state && (
                  <span className="form-error">{localErrors.state}</span>
                )}
              </div>

            </div>

            {/* API-level error */}
            {updateError && (
              <div className="alert alert-error user-profile-page__api-error">
                {updateError}
              </div>
            )}

            <div className="user-profile-page__submit">
              <button
                type="submit"
                className="btn btn-primary btn-full btn-lg"
                disabled={isUpdating}
              >
                {isUpdating ? (
                  <><span className="spinner" />Saving…</>
                ) : (
                  isFirstTime ? 'Save & Continue →' : 'Save Changes'
                )}
              </button>
            </div>

          </form>
        </div>

        {/* Skip only relevant during first-time setup */}
        {isFirstTime && (
          <button
            className="user-profile-page__skip"
            onClick={() => navigate(ROUTES.DASHBOARD)}
          >
            Skip for now
          </button>
        )}

      </div>
    </div>
  );
};

export default UserProfilePage;