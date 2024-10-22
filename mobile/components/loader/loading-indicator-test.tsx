import React from 'react';
import { render } from '@testing-library/react-native';
import { usePromiseTracker } from 'react-promise-tracker';
import LoadingIndicator from './loading-indicator';

jest.mock('react-promise-tracker', () => ({
  usePromiseTracker: jest.fn(),
}));

describe('LoadingIndicator', () => {
  it('renders correctly when promise is in progress', () => {
    (usePromiseTracker as jest.Mock).mockReturnValue({ promiseInProgress: true });

    const { getByTestId } = render(<LoadingIndicator />);
    const indicator = getByTestId('loading-indicator');

    expect(indicator).toBeTruthy();
  });

  it('does not render when promise is not in progress', () => {
    (usePromiseTracker as jest.Mock).mockReturnValue({ promiseInProgress: false });

    const { queryByTestId } = render(<LoadingIndicator />);
    const indicator = queryByTestId('loading-indicator');

    expect(indicator).toBeNull();
  });
});
