import { act, fireEvent, render } from "@testing-library/react-native";
import ChangeLanguage from "@/app/change-language";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { ReactTestInstance } from "react-test-renderer";

jest.mock('react-i18next', () => ({
	useTranslation: () => {
		return {
			t: (key: string) => key,
			i18n: {
				language: 'nl',
				changeLanguage: jest.fn(),
			}
		};
	}
}));

jest.mock('@react-native-async-storage/async-storage', () => ({
	setItem: jest.fn()
}));

jest.mock('react-native-paper', () => {
	const { Text } = require('react-native');
	const actual = jest.requireActual('react-native-paper');

	return {
		...actual,
		Icon: ({ source }: any) => (
			<Text>{source}</Text>
		)
	};
});

describe('<ChangeLanguage />', () => {
	it('should render the list of languages', () => {
		const { getByText } = render(<ChangeLanguage />);

		expect(getByText('language.en')).toBeTruthy();
		expect(getByText('language.nl')).toBeTruthy();
	});

	it('should change the language after selecting a list item', async () => {
		const { getByText } = render(<ChangeLanguage />);

		await act(() => {
			fireEvent(getByText('language.nl').parent as ReactTestInstance, 'press');
		});

		expect(AsyncStorage.setItem).toHaveBeenCalledWith('language', 'nl');
	});

	it('should log an error if language cannot be saved', async () => {
		(AsyncStorage.setItem as jest.Mock).mockRejectedValueOnce(new Error('error'));
		jest.spyOn(console, 'log').mockImplementation(jest.fn());

		const { getByText } = render(<ChangeLanguage />);

		await act(() => {
			fireEvent(getByText('language.nl').parent as ReactTestInstance, 'press');
		});

		expect(AsyncStorage.setItem).toHaveBeenCalledWith('language', 'nl');
		expect(console.log).toHaveBeenCalledWith(expect.any(Error));
		expect(getByText('check')).toBeTruthy();
	});
});